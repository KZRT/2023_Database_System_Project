package database.cli;

import database.api.ShoppingMallDatabaseTest;
import database.api.ShoppingMallDatabaseTestAPI;

class TestCreateTableMenu implements State {
    private final ShoppingMallDatabaseTestAPI api = ShoppingMallDatabaseTest.getInstance();
    private final ConsoleInterface ci;

    protected TestCreateTableMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Testing Table Creation");

        if(api.testCreatedTable()){
            System.out.println("Table Created Successfully");
        } else {
            System.out.println("Table Creation Failed");
        }
        System.out.println("For Return to Test Menu, Press 0");
    }

    @Override
    public boolean nextInput(int i) {
        ci.changeState(ci.testMenu);
        return true;
    }
}
