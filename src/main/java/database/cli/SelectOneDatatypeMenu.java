package database.cli;

import database.api.*;

import java.util.ArrayList;

class SelectOneDatatypeMenu implements TriggerState {
    private ConsoleInterface ci;
    private ShoppingMallDatabaseAPI api = ShoppingMallDatabase.getInstance();
    private ShoppingMallDatabaseTestAPI testApi = ShoppingMallDatabaseTest.getInstance();
    private ArrayList<User> users;
    private boolean trigger = false;

    protected SelectOneDatatypeMenu(ConsoleInterface ci) {
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
                users = null;
                ci.countStateOccured = false;
                ci.abortAllOperations();
                return true;
            }
            if (i == 1) {
                trigger = false;
                ci.changeUserSelectionMenu(users);
                return true;
            }
            System.out.println("Selected Query: ");
            System.out.println("SELECT * FROM users WHERE " + ShoppingMallDatabaseTest.dataTypeToQueryString(ci.getDataType(0)));
            long startTime = System.currentTimeMillis();
            users = api.selectDatatype(ci.getDataType(0));
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken: " + (endTime - startTime) + " ms");
            System.out.println("Number of users: " + users.size());
            System.out.println("Press 0 to go back to main menu");
            System.out.println("Press 1 to go User Selection Menu");
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
