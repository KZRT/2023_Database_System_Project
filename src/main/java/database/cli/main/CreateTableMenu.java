package database.cli;

import database.api.ShoppingMallDatabase;
import database.api.ShoppingMallDatabaseAPI;

class CreateTableMenu implements State {
    private ShoppingMallDatabaseAPI api = ShoppingMallDatabase.getInstance();
    private ConsoleInterface ci;

    protected CreateTableMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Table Creation");

        long startTime = System.nanoTime();
        api.createTable();
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) + " ns");
        System.out.println("For Return to Main Menu, Press 0");
    }

    @Override
    public boolean nextInput(int i) {
        ci.changeState(ci.mainMenu);
        return true;
    }
}
