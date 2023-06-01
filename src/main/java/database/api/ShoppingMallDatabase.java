package database.api;

import database.internal.Cache;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Collections.sort;

public class ShoppingMallDatabase implements ShoppingMallDatabaseAPI {
    private final Connection connection;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private static ShoppingMallDatabase instance = null;
    private final Statement statement;
    private final MessageDigest md;
    private int userFullCount = 0;
    private final Map<DataType, Integer> sexWeight;
    private final Map<DataType, Integer> memberClassWeight;
    public static final String fileLocation = "buffer/";
    private final Random random = new Random();
    private final ArrayList<DataType> notCalculatedDatatypes = new ArrayList<>();
    private final Cache cache = Cache.getInstance();

    private ShoppingMallDatabase(String database, String username, String password) throws SQLException, NoSuchAlgorithmException {
        String url = "jdbc:mysql://localhost:3306/" + database + "?serverTimezone=UTC";
        this.connection = DriverManager.getConnection(url, username, password);
        this.statement = connection.createStatement();
        this.md = MessageDigest.getInstance("SHA-256");
        this.sexWeight = new HashMap<>();
        this.memberClassWeight = new HashMap<>();
        statement.execute("USE " + database);
        statement.execute("DROP TABLE IF EXISTS `users`");

        sexWeight.put(Sex.MALE, 45);
        sexWeight.put(Sex.FEMALE, 45);
        sexWeight.put(Sex.NOT_APPLICABLE, 9);
        sexWeight.put(Sex.NOT_KNOWN, 1);

        memberClassWeight.put(MemberClass.BRONZE, 30);
        memberClassWeight.put(MemberClass.SILVER, 25);
        memberClassWeight.put(MemberClass.GOLD, 20);
        memberClassWeight.put(MemberClass.PLATINUM, 15);
        memberClassWeight.put(MemberClass.DIAMOND, 8);
        memberClassWeight.put(MemberClass.VIP, 2);
    }


    @Override
    public void createTable() {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS `users` ( "
                + "`member_number` INT UNSIGNED     NOT NULL AUTO_INCREMENT,"
                + "`id`            CHAR(20)         NOT NULL,"
                + "`password`      BINARY(32)       NOT NULL,"
                + "`name`          CHAR(20)         NOT NULL,"
                + "`address`       VARCHAR(255)     NOT NULL,"
                + "`sex`           TINYINT UNSIGNED NOT NULL,"
                + "`phone_number`  CHAR(11)         NOT NULL,"
                + "`age`           TINYINT UNSIGNED NOT NULL,"
                + "`point`         INT UNSIGNED     NOT NULL,"
                + "PRIMARY KEY (`member_number`),"
                + "UNIQUE KEY `id` (`id`));";

        try {
            statement.execute(createTableStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertData(int size) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO `users` (`id`, `password`, `name`, `address`, `sex`, `phone_number`, `age`, `point`) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < size; i++) {
            MemberClass randomPoint = (MemberClass) randomData("memberClass");
            random.nextLong(randomPoint.getMinPoint(), randomPoint.getMaxPoint());
            try {
                preparedStatement.setString(1, randomString(20));
                preparedStatement.setBinaryStream(2, new ByteArrayInputStream(getSHA256(randomString(20))));
                preparedStatement.setString(3, randomString(20));
                preparedStatement.setString(4, randomString(255));
                preparedStatement.setShort(5, ((Sex) randomData("sex")).getSexValue());
                preparedStatement.setString(6, randomString(11));
                preparedStatement.setInt(7, random.nextInt(110));
                preparedStatement.setLong(8, random.nextLong(randomPoint.getMinPoint(), randomPoint.getMaxPoint()));
                preparedStatement.execute();
            } catch (SQLException e) {
                i--;
            }
        }
        userFullCount += size;
    }

    @Override
    public void createBitmapIndex() {
        try {
            createSexBitmap();
            createAgeRangeBitmap();
            createMemberClassBitmap();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<User> selectDatatype(DataType dataType) {
        return selectDatatype(dataType.getType() + dataType.getName());
    }

    @Override
    public long countDatatype(DataType dataType) {
        return countDatatype(dataType.getType() + dataType.getName());
    }

    @Override
    public ArrayList<User> selectDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations) {
        calculateResultSet(dataTypes, operations);
        return selectDatatype("result");
    }

    @Override
    public long countDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations) {
        calculateResultSet(dataTypes, operations);
        return countDatatype("result");
    }

    private ArrayList<User> selectDatatype(String fileName) {
        ArrayList<User> users = new ArrayList<>();
        cache.isBufferAvailable(1);
        int bufferIndex = cache.acquireBuffer(fileName);
        int readCount = cache.readBuffer(bufferIndex);
        while (readCount > 0) {
            for (int i = 0; i < readCount; i++) {
                long block = cache.getNextBlock(bufferIndex);
                try {
                    users.addAll(selectFromBlock(block, cache.getIndex(bufferIndex)));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            cache.releaseBuffer(bufferIndex, false);
            cache.increaseIndex(1);
            bufferIndex = cache.acquireBuffer(fileName);
            readCount = cache.readBuffer(bufferIndex);
        }
        cache.releaseBuffer(bufferIndex, false);
        cache.releaseCache();
        return users;
    }

    private long countDatatype(String fileName) {
        long count = 0;
        cache.isBufferAvailable(1);
        int bufferIndex = cache.acquireBuffer(fileName);
        int readCount = cache.readBuffer(bufferIndex);
        while (readCount > 0) {
            for(int i = 0; i < readCount; i++){
                long buffer = cache.getNextBlock(bufferIndex);
                if(cache.isNextBlockNotAvailable(bufferIndex)) count += bitCount(buffer, cache.getIndex(bufferIndex));
                else count += bitCount(buffer);
            }
            cache.releaseBuffer(bufferIndex, false);
            cache.increaseIndex(1);
            bufferIndex = cache.acquireBuffer(fileName);
            readCount = cache.readBuffer(bufferIndex);
        }
        cache.releaseBuffer(bufferIndex, false);
        cache.releaseCache();
        return count;
    }

    private void calculateResultSet(ArrayList<DataType> dataTypes, ArrayList<Operation> operations){
        int firstBufferIndex = 0;
        int secondBufferIndex = 0;
        int resultBufferIndex = 0;
        DataType secondDataType;
        Operation operation = operations.get(0);

        String fetchingFileName = dataTypes.get(0).getType() + dataTypes.get(0).getName();
        if(operation == Operation.NOT){
            operations.remove(0);
            fetchingFileName += "Not";
            calculateNot(dataTypes.get(0));
        }
        moveToTemp(fetchingFileName);
        if (operations.size() == 0) {
            try {
                int i = 0;
                while (Files.exists(Paths.get("buffer/" + fetchingFileName + i))){
                    Files.copy(Paths.get("buffer/" + fetchingFileName + i), Paths.get("buffer/result" + i), StandardCopyOption.REPLACE_EXISTING);
                    i++;
                }
            } catch (IOException ignored) {
            }
            return;
        }

        dataTypes.remove(0);
        while (dataTypes.size() > 0){
            secondDataType = dataTypes.get(0);
            dataTypes.remove(0);
            operation = operations.get(0);
            operations.remove(0);
            fetchingFileName = secondDataType.getType() + secondDataType.getName();
            if(!operations.isEmpty() && operations.get(0) == Operation.NOT){
                operations.remove(0);
                fetchingFileName += "Not";
                calculateNot(secondDataType);
            }

            cache.isBufferAvailable(3);
            firstBufferIndex = cache.acquireBuffer("temp");
            secondBufferIndex = cache.acquireBuffer(fetchingFileName);
            resultBufferIndex = cache.acquireBuffer("result");
            cache.readBuffer(firstBufferIndex);
            int readCount = cache.readBuffer(secondBufferIndex);
            while (readCount > 0){
                for(int i = 0; i < readCount; i++){
                    long firstBlock = cache.getNextBlock(firstBufferIndex);
                    long secondBlock = cache.getNextBlock(secondBufferIndex);
                    long resultBlock = switch (operation) {
                        case AND -> firstBlock & secondBlock;
                        case OR -> firstBlock | secondBlock;
                        default -> 0;
                    };
                    if(cache.writeBlockToBuffer(resultBufferIndex, resultBlock)){
                        cache.releaseBuffer(firstBufferIndex, false);
                        cache.releaseBuffer(secondBufferIndex, false);
                        cache.releaseBuffer(resultBufferIndex, true);
                        cache.increaseIndex(1);
                        cache.isBufferAvailable(3);
                        firstBufferIndex = cache.acquireBuffer("temp");
                        secondBufferIndex = cache.acquireBuffer(fetchingFileName);
                        resultBufferIndex = cache.acquireBuffer("result");
                        readCount = cache.readBuffer(firstBufferIndex);
                        cache.readBuffer(secondBufferIndex);
                    }
                }
            }
            cache.releaseBuffer(firstBufferIndex, false);
            cache.releaseBuffer(secondBufferIndex, false);
            cache.releaseBuffer(resultBufferIndex, true);
            cache.releaseCache();
            moveToTemp("result");
        }
    }

    private void moveToTemp(String fetchingFileName){
        try {
            int i = 0;
            while (Files.exists(Paths.get("buffer/" + fetchingFileName + i))){
                Files.copy(Paths.get("buffer/" + fetchingFileName + i), Paths.get("buffer/temp" + i), StandardCopyOption.REPLACE_EXISTING);
                i++;
            }
        } catch (IOException ignored) {
        }
    }

    private String randomString(int size) {
        int leftLimit = '0';
        int rightLimit = 'z';
        return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public byte[] getSHA256(String input) {
        md.update(input.getBytes());
        return md.digest();
    }

    private DataType randomData(String datatype) {
        // weight random, weights in sexWeight
        int pick = random.nextInt(100);
        Map<DataType, Integer> weight;
        switch (datatype) {
            case "sex" -> weight = sexWeight;
            case "memberClass" -> weight = memberClassWeight;
            default -> weight = null;
        }
        // seeks through sexWeight, add until pick is smaller than the sum of weights
        int sum = 0;
        for (Map.Entry<DataType, Integer> entry : Objects.requireNonNull(weight).entrySet()) {
            sum += entry.getValue();
            if (pick < sum) {
                return entry.getKey();
            }
        }
        return Sex.MALE;
    }

    private void createSexBitmap() throws SQLException {
        for (Sex sex : Sex.values()) {
            int index = 0;
            cache.isBufferAvailable(1);
            index = cache.acquireBuffer(sex.getType() + sex.getName());
            ResultSet resultSet = statement.executeQuery("SELECT `sex` FROM `users`");
            resultSet.next();
            boolean flag = true;
            while (flag) {
                long block = 0;
                for (int i = 0; i < 64; i++) {
                    block |= (resultSet.getShort(1) == sex.getSexValue()) ? 1 : 0;
                    if (!resultSet.next()) {
                        flag = false;
                        block <<= 64 - i - 1;
                        break;
                    }
                    if (i == 63) break;
                    block <<= 1;
                }
                if(cache.writeBlockToBuffer(index, block)){
                    cache.releaseBuffer(index, true);
                    cache.increaseIndex(1);
                    cache.isBufferAvailable(1);
                    index = cache.acquireBuffer(sex.getType() + sex.getName());
                }
            }
            cache.releaseBuffer(index, true);
            cache.releaseCache();
        }
    }

    private void createAgeRangeBitmap() throws SQLException {
        for (AgeRange ageRange : AgeRange.values()) {
            int index = 0;
            cache.isBufferAvailable(1);
            index = cache.acquireBuffer(ageRange.getType() + ageRange.getName());
            ResultSet resultSet = statement.executeQuery("SELECT `age` FROM `users`");
            resultSet.next();
            boolean flag = true;
            while (flag) {
                long block = 0;
                for (int i = 0; i < 64; i++) {
                    block |= (AgeRange.getAgeRange(resultSet.getShort(1)) == ageRange) ? 1 : 0;
                    if (!resultSet.next()) {
                        flag = false;
                        block <<= 64 - i - 1;
                        break;
                    }
                    if (i == 63) break;
                    block <<= 1;
                }
                if(cache.writeBlockToBuffer(index, block)){
                    cache.releaseBuffer(index, true);
                    cache.increaseIndex(1);
                    cache.isBufferAvailable(1);
                    index = cache.acquireBuffer(ageRange.getType() + ageRange.getName());
                }
            }
            cache.releaseBuffer(index, true);
            cache.releaseCache();
        }
    }

    private void createMemberClassBitmap() throws SQLException {
        for (MemberClass memberClass : MemberClass.values()) {
            int index = 0;
            cache.isBufferAvailable(1);
            index = cache.acquireBuffer(memberClass.getType() + memberClass.getName());
            ResultSet resultSet = statement.executeQuery("SELECT `point` FROM `users`");
            resultSet.next();
            boolean flag = true;
            while (flag) {
                long block = 0;
                for (int i = 0; i < 64; i++) {
                    block |= (MemberClass.getMemberClass(resultSet.getLong(1)) == memberClass) ? 1 : 0;
                    if (!resultSet.next()) {
                        flag = false;
                        block <<= 64 - i - 1;
                        break;
                    }
                    if (i == 63) break;
                    block <<= 1;
                }
                if(cache.writeBlockToBuffer(index, block)){
                    cache.releaseBuffer(index, true);
                    cache.increaseIndex(1);
                    cache.isBufferAvailable(1);
                    index = cache.acquireBuffer(memberClass.getType() + memberClass.getName());
                }
            }
            cache.releaseBuffer(index, true);
            cache.releaseCache();
        }
    }

    private void calculateNot(DataType dataType){
        if(notCalculatedDatatypes.contains(dataType)) return;
        notCalculatedDatatypes.add(dataType);
        cache.isBufferAvailable(2);
        int bufferIndex = cache.acquireBuffer(dataType.getType() + dataType.getName());
        int writerIndex = cache.acquireBuffer(dataType.getType() + dataType.getName() + "Not");
        int readCount = cache.readBuffer(bufferIndex);
        while (readCount > 0) {
            long block = cache.getNextBlock(bufferIndex);
            block = ~block;
            if(cache.writeBlockToBuffer(writerIndex, block)){
                cache.releaseBuffer(writerIndex, true);
                cache.releaseBuffer(bufferIndex, false);
                cache.increaseIndex(1);
                cache.isBufferAvailable(2);

                bufferIndex = cache.acquireBuffer(dataType.getType() + dataType.getName());
                writerIndex = cache.acquireBuffer(dataType.getType() + dataType.getName() + "Not");
                readCount = cache.readBuffer(bufferIndex);
            }
        }
        cache.releaseBuffer(writerIndex, true);
        cache.releaseBuffer(bufferIndex, false);
        cache.releaseCache();
    }
    private ArrayList<User> selectFromBlock(long block, int index) throws SQLException {
        ArrayList<User> result = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `member_number` = ?;");
        for (int i = 0; i < 64; i++) {
            if ((block & (1L << i)) != 0) {
                if(index * 64L + 64 - i > userFullCount) continue;
                preparedStatement.setLong(1, index * 64L + 64 - i);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                User user = new User(resultSet.getLong(1), resultSet.getString(2), resultSet.getBytes(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getShort(6),
                        resultSet.getString(7), resultSet.getShort(8), resultSet.getLong(9));
                result.add(user);
            }
        }
        sort(result);
        return result;
    }

    public static int bitCount(long i) {
        i = i - ((i >>> 1) & 0x5555555555555555L);
        i = (i & 0x3333333333333333L) + ((i >>> 2) & 0x3333333333333333L);
        i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
        i = i + (i >>> 8);
        i = i + (i >>> 16);
        i = i + (i >>> 32);
        return (int) i & 0x7f;
    }

    public int bitCount(long block, int index){
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if ((block & (1L << i)) != 0) {
                if (index * 64L + 64 - i > this.userFullCount) continue;
                count++;
            }
        }
        return count;
    }

    public static ShoppingMallDatabase getInstance(String databaseName, String username, String password) {
        if (instance == null) {
            try {
                instance = new ShoppingMallDatabase(databaseName, username, password);
            } catch (SQLException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static ShoppingMallDatabase getInstance() {
        if (instance == null) throw new NullPointerException("ShoppingMallDatabase is not initialized");
        return instance;
    }
}
