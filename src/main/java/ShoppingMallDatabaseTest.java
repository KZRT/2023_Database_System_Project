import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ShoppingMallDatabaseTest implements ShoppingMallDatabaseTestAPI {
    private final Connection connection;
    private static ShoppingMallDatabaseTest instance = null;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String database = "shopping_mall";
    private final Statement statement;
    private final ArrayList<String> tableColumns;
    private final ShoppingMallDatabaseAPI shoppingMallDatabaseAPI = ShoppingMallDatabase.getInstance();
    private int rowCount;
    private final ArrayList<DataType> testDatatypes;

    ShoppingMallDatabaseTest() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/" + database + "?serverTimezone=UTC";
        String user = "db_test";
        this.rowCount = 0;
        this.connection = DriverManager.getConnection(url, user, user);
        this.statement = connection.createStatement();
        statement.execute("USE " + database);
        tableColumns = new ArrayList<>();
        tableColumns.add("member_number");
        tableColumns.add("id");
        tableColumns.add("password");
        tableColumns.add("name");
        tableColumns.add("address");
        tableColumns.add("sex");
        tableColumns.add("phone_number");
        tableColumns.add("age");
        tableColumns.add("point");

        testDatatypes = new ArrayList<>();
        testDatatypes.addAll(Arrays.asList(Sex.values()));
        testDatatypes.addAll(Arrays.asList(AgeRange.values()));
        testDatatypes.addAll(Arrays.asList(MemberClass.values()));
    }

    @Override
    public void createTable() {
        System.out.println("Create Table Cannot be used in Test Mode");
    }

    @Override
    public void insertData(int size) {
        rowCount += size;
    }

    @Override
    public void createBitmapIndex() {

    }

    @Override
    public ArrayList<User> selectDatatype(DataType dataType) {
        return new ArrayList<>();
    }

    @Override
    public long countDatatype(DataType dataType) {
        return 0;
    }

    @Override
    public ArrayList<User> selectDatatypeWithOperation(DataType[] dataType, Operation[] operation) {
        return new ArrayList<>();
    }

    @Override
    public long countDatatypeWithOperation(DataType[] dataType, Operation[] operation) {
        return 0;
    }


    @Override
    public boolean testCreatedTable() {
        ResultSet resultSet = null;
        String showTableStatement = "SHOW TABLES";
        try {
            resultSet = statement.executeQuery(showTableStatement);
            if (resultSet.next()) {
                String tableName = resultSet.getString(1);
                if (!tableName.equals("users")) {
                    return false;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        String showColumnsStatement = "SHOW COLUMNS FROM users";
        try {
            resultSet = statement.executeQuery(showColumnsStatement);
            for (String columnName : tableColumns){
                if (!resultSet.next()) {
                    return false;
                }
                String column = resultSet.getString(1);
                if (!column.equals(columnName)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean testInsertedData() {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count != rowCount) {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean testCreatedBitmapIndex() {
        ResultSet resultSet = null;
        for (DataType dataType : testDatatypes) {
            try {
                long count = shoppingMallDatabaseAPI.countDatatype(dataType);
                switch (dataType.getType()){
                    case "Sex" -> resultSet = statement.executeQuery("SELECT COUNT(*) FROM `users` WHERE `sex` = " + ((Sex)dataType).getSexValue());
                    case "AgeRange" -> resultSet = statement.executeQuery("SELECT COUNT(*) FROM `users` WHERE `age` >= " + ((AgeRange)dataType).getMinAge() + " AND `age` <= " + ((AgeRange)dataType).getMaxAge());
                    case "MemberClass" -> resultSet = statement.executeQuery("SELECT COUNT(*) FROM `users` WHERE `point` >= " + ((MemberClass)dataType).getMinPoint() + " AND `point` <= " + ((MemberClass)dataType).getMaxPoint());
                    default -> throw new RuntimeException("Invalid DataType");
                }
                resultSet.next();
                if (count != resultSet.getLong(1)) {
                    return false;
                }
                ArrayList<User> users = shoppingMallDatabaseAPI.selectDatatype(dataType);
                switch (dataType.getType()){
                    case "Sex" -> resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `sex` = " + ((Sex)dataType).getSexValue());
                    case "AgeRange" -> resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `age` >= " + ((AgeRange)dataType).getMinAge() + " AND `age` <= " + ((AgeRange)dataType).getMaxAge());
                    case "MemberClass" -> resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `point` >= " + ((MemberClass)dataType).getMinPoint() + " AND `point` <= " + ((MemberClass)dataType).getMaxPoint());
                }
                resultSet.next();
                for(User user: users){
                    if(user.getMemberNumber() != resultSet.getLong(1)){
                        System.out.println(dataType.getType() + " " + dataType.getName());
                        System.out.println(user.getMemberNumber() + " " + resultSet.getLong(1));
                        return false;
                    }
                    resultSet.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public boolean testSelectedDatatype(DataType dataType, User[] output) {
        return false;
    }

    @Override
    public boolean testCountDatatype(DataType dataType, long output) {
        return false;
    }

    @Override
    public boolean testSelectedDatatypeWithOperation(DataType[] dataType, Operation[] operation, User[] output) {
        return false;
    }

    @Override
    public boolean testCountDatatypeWithOperation(DataType[] dataType, Operation[] operation, long output) {
        return false;
    }

    public static ShoppingMallDatabaseTest getInstance() {
        if (instance == null) {
            try{
                instance = new ShoppingMallDatabaseTest();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}

