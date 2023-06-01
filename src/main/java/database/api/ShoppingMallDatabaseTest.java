package database.api;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ShoppingMallDatabaseTest implements ShoppingMallDatabaseTestAPI {
    private final Connection connection;
    private static ShoppingMallDatabaseTest instance = null;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final Statement statement;
    private final ArrayList<String> tableColumns;
    private final ShoppingMallDatabaseAPI shoppingMallDatabaseAPI = ShoppingMallDatabase.getInstance();
    private int rowCount;
    private final ArrayList<DataType> testDatatypes;

    ShoppingMallDatabaseTest(String database, String username, String password) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/" + database + "?serverTimezone=UTC";
        this.rowCount = 0;
        this.connection = DriverManager.getConnection(url, username, password);
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
    public void insertData(int size) {
        rowCount += size;
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
            for (String columnName : tableColumns) {
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
        boolean flag = true;
        for (DataType dataType : testDatatypes) {
            try {
                long count = shoppingMallDatabaseAPI.countDatatype(dataType);
                resultSet = statement.executeQuery("SELECT COUNT(*) FROM `users` WHERE " + dataTypeToQueryString(dataType));
                resultSet.next();
                if (count != resultSet.getLong(1)) {
                    System.out.println(dataType.getType() + " " + dataType.getName());
                    System.out.println("Count Wrong: " + count + " Expected: " + resultSet.getLong(1));
                    flag = false;
                }
                ArrayList<User> users = shoppingMallDatabaseAPI.selectDatatype(dataType);
                resultSet = statement.executeQuery("SELECT * FROM `users` WHERE " + dataTypeToQueryString(dataType));
                resultSet.next();
                for (User user : users) {
                    if (user.getMemberNumber() != resultSet.getLong(1)) {
                        System.out.println(dataType.getType() + " " + dataType.getName());
                        System.out.println(user.getMemberNumber() + " " + resultSet.getLong(1));
                        flag = false;
                    }
                    resultSet.next();
                }
                System.out.println("Create Bitmap Test on: " + dataType.getType() + " " + dataType.getName() + " Passed");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return flag;
    }

    @Override
    public boolean testSelectedDatatype(DataType dataType, ArrayList<User> output) {
        boolean flag = true;
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM `users` WHERE " + dataTypeToQueryString(dataType));
            resultSet.next();
            for (User user : output) {
                if (user.getMemberNumber() != resultSet.getLong(1)) {
                    System.out.println("Failed on: " + dataTypeToQueryString(dataType));
                    System.out.println(user.getMemberNumber() + " " + resultSet.getLong(1));
                    flag = false;
                }
                resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public boolean testCountDatatype(DataType dataType, long output) {
        boolean flag = true;
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM `users` WHERE " + dataTypeToQueryString(dataType));
            resultSet.next();
            if (output != resultSet.getLong(1)) {
                System.out.println("Failed on: " + dataTypeToQueryString(dataType));
                System.out.println("Count Wrong: " + output + " Expected: " + resultSet.getLong(1));
                flag = false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public boolean testSelectedDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations, ArrayList<User> output) {
        boolean flag = true;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM `users` WHERE ";
            if (operations.get(0) == Operation.NOT) {
                query += "NOT ";
                operations.remove(0);
            }
            for (DataType type : dataTypes) {
                if (!operations.isEmpty() && operations.get(0) == Operation.NOT) {
                    query += " NOT ";
                    operations.remove(0);
                }
                query += dataTypeToQueryString(type);
                if (operations.isEmpty()) break;
                switch (operations.get(0)) {
                    case AND -> query += " AND ";
                    case OR -> query += " OR ";
                    case NOT -> query += " NOT ";
                }
                operations.remove(0);
            }
            System.out.println(query);
            resultSet = statement.executeQuery(query);
            resultSet.next();
            for (User user : output) {
                if (user.getMemberNumber() != resultSet.getLong(1)) {
                    System.out.println("Failed on: " + query);
                    System.out.println(user.getMemberNumber() + " " + resultSet.getLong(1));
                    flag = false;
                }
                resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public boolean testCountDatatypeWithOperation(ArrayList<DataType> dataTypes, ArrayList<Operation> operations, long output) {
        boolean flag = true;
        ResultSet resultSet = null;
        try {
            String query = "SELECT COUNT(*) FROM `users` WHERE ";
            if (operations.get(0) == Operation.NOT) {
                query += "NOT ";
                operations.remove(0);
            }
            for (DataType type : dataTypes) {
                if (!operations.isEmpty() && operations.get(0) == Operation.NOT) {
                    query += " NOT ";
                    operations.remove(0);
                }
                query += dataTypeToQueryString(type);
                if (operations.isEmpty()) break;
                switch (operations.get(0)) {
                    case AND -> query += " AND ";
                    case OR -> query += " OR ";
                    case NOT -> query += " NOT ";
                }
                operations.remove(0);
            }
            System.out.println(query);
            resultSet = statement.executeQuery(query);
            resultSet.next();
            if (output != resultSet.getLong(1)) {
                System.out.println("Failed on: " + query);
                System.out.println(output + " " + resultSet.getLong(1));
                flag = false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    public static String dataTypeToQueryString(DataType dataType) {
        return switch (dataType.getType()) {
            case "Sex" -> "`sex` = " + ((Sex) dataType).getSexValue();
            case "AgeRange" ->
                    "`age` BETWEEN " + ((AgeRange) dataType).getMinAge() + " AND " + ((AgeRange) dataType).getMaxAge();
            case "MemberClass" ->
                    "`point` BETWEEN " + ((MemberClass) dataType).getMinPoint() + " AND " + ((MemberClass) dataType).getMaxPoint();
            default -> throw new RuntimeException("Invalid DataType");
        };
    }

    public static ShoppingMallDatabaseTest getInstance(String databaseName, String username, String password) {
        if (instance == null) {
            try {
                instance = new ShoppingMallDatabaseTest(databaseName, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static ShoppingMallDatabaseTest getInstance() {
        if (instance == null) throw new NullPointerException("ShoppingMallDatabaseTest is not initialized");
        return instance;
    }

}

