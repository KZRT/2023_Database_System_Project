package database.cli;

import database.api.ShoppingMallDatabaseTest;
import database.api.ShoppingMallDatabaseTestAPI;

class TestInsertDataMenu implements State {
    private final ShoppingMallDatabaseTestAPI api = ShoppingMallDatabaseTest.getInstance();
    private final ConsoleInterface ci;

    protected TestInsertDataMenu(ConsoleInterface ci) {
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
        ci.changeState(ci.testMenu);
        return true;
    }
}
