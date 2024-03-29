package database.cli.test;

import database.api.ShoppingMallDatabase;
import database.api.ShoppingMallDatabaseAPI;
import database.api.ShoppingMallDatabaseTest;
import database.api.ShoppingMallDatabaseTestAPI;

class TestCountOneDatatypeMenu implements TriggerState {
    private TestConsoleInterface ci;
    private ShoppingMallDatabaseAPI api = ShoppingMallDatabase.getInstance();
    private ShoppingMallDatabaseTestAPI testApi = ShoppingMallDatabaseTest.getInstance();
    private boolean trigger = false;

    protected TestCountOneDatatypeMenu(TestConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Selecting Datatype");
        System.out.println("Please select an option:");
        System.out.println("1. Select Sex");
        System.out.println("2. Select Age Range");
        System.out.println("3. Select Member's Class");
        System.out.println("4. Return to Previous Menu");
        System.out.println("5. Abort All Operations");
    }

    @Override
    public boolean nextInput(int i) {
        if (trigger) {
            if (i == 0) {
                trigger = false;
                ci.changeState(ci.mainMenu);
                ci.countStateOccured = false;
                ci.abortAllOperations();
                return true;
            }
            System.out.println("Testing Count One Datatype");
            System.out.println("Selected Query: ");
            System.out.println("SELECT COUNT(*) FROM users WHERE " + ShoppingMallDatabaseTest.dataTypeToQueryString(ci.getDataType(0)));

            long count = api.countDatatype(ci.getDataType(0));
            System.out.println("Count: " + count);
            if (testApi.testCountDatatype(ci.getDataType(0), count)) {
                System.out.println("Test Passed");
            } else {
                System.out.println("Test Failed");
            }
            System.out.println("For Return to Test Menu, Press 0");
            return false;
        }
        switch (i) {
            case 1:
                ci.changeState(ci.selectSexMenu);
                return true;
            case 2:
                ci.changeState(ci.selectAgeRangeMenu);
                return true;
            case 3:
                ci.changeState(ci.selectMemberClassMenu);
                return true;
            case 4:
                ci.changeState(ci.mainMenu);
                return true;
            case 5:
                ci.abortAllOperations();
                ci.changeState(ci.mainMenu);
                return true;
        }
        return false;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

}

