import database.api.ShoppingMallDatabase;
import database.api.ShoppingMallDatabaseTest;
import database.cli.ConsoleInterface;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the database system!");
        System.out.println("Please enter your database name:");
        String databaseName = new Scanner(System.in).nextLine().trim();
        System.out.println("Please enter your username:");
        String username = new Scanner(System.in).nextLine().trim();
        System.out.println("Please enter your password:");
        String password = new Scanner(System.in).nextLine().trim();

        ShoppingMallDatabase.getInstance(databaseName, username, password);
        ShoppingMallDatabaseTest.getInstance(databaseName, username, password);

        ConsoleInterface ci = ConsoleInterface.getInstance();
//        ShoppingMallDatabaseAPI shoppingMallDatabaseAPI = ShoppingMallDatabase.getInstance();
//        shoppingMallDatabaseAPI.createTable();
//        ShoppingMallDatabaseTestAPI shoppingMallDatabaseTestAPI = ShoppingMallDatabaseTest.getInstance();
//        System.out.println(shoppingMallDatabaseTestAPI.testCreatedTable());
//        shopping1
//      MallDatabaseAPI.insertData(2000);
//        shoppingMallDatabaseTestAPI.insertData(2000);
//        System.out.println(shoppingMallDatabaseTestAPI.testInsertedData());
//        shoppingMallDatabaseAPI.createBitmapIndex();
//        System.out.println(shoppingMallDatabaseTestAPI.testCreatedBitmapIndex());
//
//        ArrayList<DataType> testDatatypes = new ArrayList<>();
//        testDatatypes.add(Sex.MALE);
//        testDatatypes.add(AgeRange.TEEN);
//        testDatatypes.add(MemberClass.GOLD);
//
//        ArrayList<Operation> testOperations = new ArrayList<>();
//        testOperations.add(Operation.NOT);
//        testOperations.add(Operation.AND);
//        testOperations.add(Operation.NOT);
//        testOperations.add(Operation.OR);
//
//        ArrayList<DataType> testDatatypes1 = new ArrayList<>(testDatatypes);
//        ArrayList<Operation> testOperations1 = new ArrayList<>(testOperations);
//
//        ArrayList<User> users2 = shoppingMallDatabaseAPI.selectDatatypeWithOperation(testDatatypes1, testOperations1);
//        testDatatypes1.addAll(testDatatypes);
//        testOperations1.addAll(testOperations);
//        System.out.println(shoppingMallDatabaseTestAPI.testSelectedDatatypeWithOperation(testDatatypes1, testOperations1, users2));
//        testOperations1.addAll(testOperations);
//        long result = shoppingMallDatabaseAPI.countDatatypeWithOperation(testDatatypes1, testOperations1);
//        testDatatypes1.addAll(testDatatypes);
//        testOperations1.addAll(testOperations);
//        System.out.println(shoppingMallDatabaseTestAPI.testCountDatatypeWithOperation(testDatatypes1, testOperations1, result));
    }

    public static void test(int i){
        System.out.println(i);
    }
}