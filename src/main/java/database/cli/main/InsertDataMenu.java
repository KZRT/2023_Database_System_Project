package database.cli;


import database.api.ShoppingMallDatabase;
import database.api.ShoppingMallDatabaseAPI;
import database.api.ShoppingMallDatabaseTest;
import database.api.ShoppingMallDatabaseTestAPI;

class InsertDataMenu implements State {
    private ShoppingMallDatabaseAPI api = ShoppingMallDatabase.getInstance();
    private ShoppingMallDatabaseTestAPI testApi = ShoppingMallDatabaseTest.getInstance();
    private ConsoleInterface ci;
    long fullCount = 0;

    protected InsertDataMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Data Insertion");
        System.out.println("Please enter the number of rows to insert: ");
        System.out.println("For Return to Main Menu, Press 0");
    }

    @Override
    public boolean nextInput(int i) {
        if (i == 0) {
            ci.changeState(ci.mainMenu);
            return true;
        }
        fullCount += i;
        if (fullCount > Integer.MAX_VALUE) {
            System.out.println("Too Many Rows!");
            fullCount -= i;
            System.out.println("Current Row Count: " + fullCount);
            System.out.println("Maximum Row Count: " + Integer.MAX_VALUE);
            System.out.println("Please enter the number of rows to insert: ");
            System.out.println("For Return to Main Menu, Press 0");
            return false;
        }
        System.out.println("Inserting " + i + " rows...");
        long startTime = System.nanoTime();
        api.insertData(i);
        testApi.insertData(i);
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) + " ns");
        printScreen();
        return false;
    }
}
