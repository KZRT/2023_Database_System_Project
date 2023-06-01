package database.cli.test;

import database.api.ShoppingMallDatabaseTest;
import database.api.ShoppingMallDatabaseTestAPI;

class TestInsertDataMenu implements State {
    private final ShoppingMallDatabaseTestAPI api = ShoppingMallDatabaseTest.getInstance();
    private final TestConsoleInterface ci;

    protected TestInsertDataMenu(TestConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Testing Data Insertion");

        if(api.testInsertedData()){
            System.out.println("Data Inserted Successfully");
        } else {
            System.out.println("Data Insertion Failed");
        }
        System.out.println("For Return to Test Menu, Press 0");
    }

    @Override
    public boolean nextInput(int i) {
        ci.changeState(ci.mainMenu);
        return true;
    }
}
