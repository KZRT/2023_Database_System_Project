import database.internal.BufferType;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Collections.sort;


public class ShoppingMallDatabase implements ShoppingMallDatabaseAPI {
    private final Connection connection;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String database = "shopping_mall";
    private static ShoppingMallDatabase instance = null;
    private final Statement statement;
    private final Random random = new Random();
    private final MessageDigest md;
    private final Map<DataType, Integer> sexWeight;
    private final Map<DataType, Integer> memberClassWeight;
    private final Cache cache = Cache.getInstance();
    private final ArrayList<DataType> notCalculatedDatatypes = new ArrayList<>();
    private static final int CACHE_SIZE = 4;
    private static final int BUFFER_SIZE = 4;

    private ShoppingMallDatabase() throws SQLException, NoSuchAlgorithmException {
        String url = "jdbc:mysql://localhost:3306/" + database + "?serverTimezone=UTC";
        String user = "db_project";
        this.connection = DriverManager.getConnection(url, user, user);
        this.statement = connection.createStatement();
        this.md = MessageDigest.getInstance("SHA-256");
        this.sexWeight = new HashMap<>();
        this.memberClassWeight = new HashMap<>();
        this.buffers = new ArrayList<>();
        statement.execute("USE " + database);
//        statement.execute("DROP TABLE IF EXISTS `users`");

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
        buffers.clear();
        int index = 0;
        int bufferIndex = 0;
        ArrayList<User> users = new ArrayList<>();
        while (readBuffer(dataType, index, bufferIndex)) {
            long buffer = this.buffers.get(bufferIndex).block();
            try {
                users.addAll(selectFromBuffer(buffer, index));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            index++;
            bufferIndex++;
            if (bufferIndex == BUFFER_SIZE) {
                bufferIndex = 0;
            }
        }
        return users;
    }

    @Override
    public long countDatatype(DataType dataType) {
        buffers.clear();
        long count = 0;
        int index = 0;
        int bufferIndex = 0;
        while (readBuffer(dataType, index, bufferIndex)) {
            long buffer = this.buffers.get(bufferIndex).block();
            count += bitCount(buffer);
            index++;
            bufferIndex++;
            if (bufferIndex == BUFFER_SIZE) {
                bufferIndex = 0;
            }
        }
        return count;
    }

    @Override
    public ArrayList<User> selectDatatypeWithOperation(DataType[] dataType, Operation[] operation) {
        int index = 0;
        int bufferIndex = 0;
        ArrayList<User> users = new ArrayList<>();
        int querySize = dataType.length;
        for (int i = 0; i < querySize; i++) {
            if(operation[i] == Operation.NOT){
                if(notCalculatedDatatypes.contains(dataType[i])){
                    continue;
                }
                notCalculatedDatatypes.add(dataType[i]);

            }
        }
        return users;
    }

    @Override
    public long countDatatypeWithOperation(DataType[] dataType, Operation[] operation) {
        return 0;
    }

    public static ShoppingMallDatabase getInstance() {
        if (instance == null) {
            try {
                instance = new ShoppingMallDatabase();
            } catch (SQLException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return instance;
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

    public DataType randomData(String datatype) {
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

    private void writeBuffer(String fileName, int index) {
        File file = new File(fileLocation + fileName + index);
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            dataOutputStream.writeLong(buffers.get(index).block());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFullBuffer(DataType dataType){
        for(BufferStatus bufferStatus : buffers){
            File file = new File(fileLocation + dataType.getType() + dataType.getName() + bufferStatus.index());
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
                dataOutputStream.writeLong(bufferStatus.block());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean readBuffer(DataType dataType, int index, int at) {
        File file = new File(fileLocation + dataType.getType() + dataType.getName() + index);
        if (!file.exists()) return false;
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            buffers.add(at, new BufferStatus(dataInputStream.readLong(), index));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


//    private boolean isCacheFull(){
//            for(BufferStatus bufferStatus : buffers){
//                if(bufferStatus.type() == BufferType.FREE) return false;
//            }
//        }
//        return true;
//    }
//    private boolean addToCache(ArrayList<BufferStatus> buffer, boolean write, String fileName, int cacheIndex, int index){
//        if(isCacheFull()){
//            for (ArrayList<BufferStatus> bufferStatuses : cache) {
//                for(BufferStatus bufferStatus : bufferStatuses){
//                    if(bufferStatus.type() == BufferType.WRITE){
//                        writeBuffer(fileName, bufferStatus.index());
//                        bufferStatus.type(BufferType.FREE);
//                    }
//                }
//        }
//    }

    private void createSexBitmap() throws SQLException {
        for (Sex sex : Sex.values()) {
            int index = 0;
            int bufferIndex = 0;
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
                if (this.buffers.size() >= BUFFER_SIZE) {
                    writeFullBuffer(sex);
                }
                this.buffers.add(new BufferStatus(block, index++, true));
            }
            writeFullBuffer(sex);
        }
    }

    private void createAgeRangeBitmap() throws SQLException {
        for (AgeRange ageRange : AgeRange.values()) {
            int index = 0;
            ResultSet resultSet = statement.executeQuery("SELECT `age` FROM `users`");
            resultSet.next();
            boolean flag = true;
            while (flag) {
                long buffer = 0;
                for (int i = 0; i < 64; i++) {
                    buffer |= (AgeRange.getAgeRange(resultSet.getShort(1)) == ageRange) ? 1 : 0;
                    if (!resultSet.next()) {
                        flag = false;
                        buffer <<= 64 - i - 1;
                        break;
                    }
                    if (i == 63) break;
                    buffer <<= 1;
                }
                if (this.buffers.size() >= BUFFER_SIZE) {
                    writeFullBuffer(ageRange);
                }
                this.buffers.add(new BufferStatus(buffer, index++, true));
            }
            writeFullBuffer(ageRange);
        }
    }

    private void createMemberClassBitmap() throws SQLException {
        for (MemberClass memberClass : MemberClass.values()) {
            int index = 0;
            ResultSet resultSet = statement.executeQuery("SELECT `point` FROM `users`");
            resultSet.next();
            boolean flag = true;
            while (flag) {
                long buffer = 0;
                for (int i = 0; i < 64; i++) {
                    buffer |= (MemberClass.getMemberClass(resultSet.getLong(1)) == memberClass) ? 1 : 0;
                    if (!resultSet.next()) {
                        flag = false;
                        buffer <<= 64 - i - 1;
                        break;
                    }
                    if (i == 63) break;
                    buffer <<= 1;
                }
                if (this.buffers.size() >= BUFFER_SIZE) {
                    writeFullBuffer(memberClass);
                }
                this.buffers.add(new BufferStatus(buffer, index++, true));
            }
            writeFullBuffer(memberClass);
        }
    }

    private void calculateNot(DataType dataType){
        notCalculatedDatatypes.add(dataType);
        int index = 0;
        int bufferIndex = 0;
        while (readBuffer(dataType, index, bufferIndex)) {
            buffers.add(bufferIndex + 1, new BufferStatus(~buffers.get(bufferIndex).block(), index, true));
            index++;
            bufferIndex++;
            if(bufferIndex >= BUFFER_SIZE){
                writeFullBuffer(dataType);
            }
        }
    }

    private ArrayList<User> selectFromBuffer(long buffer, int index) throws SQLException {
        ArrayList<User> result = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `member_number` = ?;");
        for (int i = 0; i < 64; i++) {
            if ((buffer & (1L << i)) != 0) {
                preparedStatement.setLong(1, index * 64 + 64 - i);
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
}
