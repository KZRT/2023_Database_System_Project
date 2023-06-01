package database.cli.test;

import database.api.ShoppingMallDatabaseTest;
import database.api.ShoppingMallDatabaseTestAPI;

class TestCreateBitmapIndexMenu implements State {
    private ShoppingMallDatabaseTestAPI api = ShoppingMallDatabaseTest.getInstance();
    private TestConsoleInterface ci;

    protected TestCreateBitmapIndexMenu(TestConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Testing Bitmap Index Creation");

        if(api.testCreatedBitmapIndex()){
            System.out.println("Bitmap Index Created Successfully");
        } else {
            System.out.println("Bitmap Index Creation Failed");
        }
        System.out.println("For Return to Test Menu, Press 0");
    }

    @Override
    public boolean nextInput(int i) {
        ci.changeState(ci.mainMenu);
        return true;
    }
}
