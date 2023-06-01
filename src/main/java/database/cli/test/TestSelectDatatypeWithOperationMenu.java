package database.cli.test;

import database.api.*;

import java.util.ArrayList;

import static database.api.ShoppingMallDatabaseTest.dataTypeToQueryString;

class TestSelectDatatypeWithOperationMenu implements TriggerState {
    private TestConsoleInterface ci;
    private ShoppingMallDatabaseAPI api = ShoppingMallDatabase.getInstance();
    private ShoppingMallDatabaseTestAPI testApi = ShoppingMallDatabaseTest.getInstance();
    private ArrayList<User> users;
    private boolean notUsed = false;
    private boolean queryEnd = false;
    private boolean trigger = false;

    protected TestSelectDatatypeWithOperationMenu(TestConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        notUsed = false;
        ci.triggerStateOccured = true;
        ci.multiDatatypeStateOccured = true;
        System.out.println("Selecting Datatype");
        System.out.println("Please select an option:");
        System.out.println("1. NOT");
        System.out.println("2. Select Sex");
        System.out.println("3. Select Age Range");
        System.out.println("4. Select Member's Class");
        System.out.println("5. Abort All Operations");
    }

    @Override
    public boolean nextInput(int i) {
        if (queryEnd) {
            if (i == 0) {
                queryEnd = false;
                ci.changeState(ci.mainMenu);
                users = null;
                ci.countStateOccured = false;
                ci.abortAllOperations();
                return true;
            }
        }
        if (trigger) {
            System.out.println("Testing Select Datatypes With Operations");
            System.out.println("Selected Query: ");
            ArrayList<DataType> tempDataTypes = new ArrayList<>(ci.getDataTypes());
            ArrayList<Operation> tempOperations = new ArrayList<>(ci.getOperations());
            StringBuilder query = new StringBuilder("SELECT * FROM `users` WHERE ");
            if (tempOperations.get(0) == Operation.NOT) {
                query.append("NOT ");
                tempOperations.remove(0);
            }
            for (DataType type : tempDataTypes) {
                if (!tempOperations.isEmpty() && tempOperations.get(0) == Operation.NOT) {
                    query.append(" NOT ");
                    tempOperations.remove(0);
                }
                query.append(dataTypeToQueryString(type));
                if (tempOperations.isEmpty()) break;
                switch (tempOperations.get(0)) {
                    case AND -> query.append(" AND ");
                    case OR -> query.append(" OR ");
                    case NOT -> query.append(" NOT ");
                }
                tempOperations.remove(0);
            }
            tempDataTypes = new ArrayList<>(ci.getDataTypes());
            tempOperations = new ArrayList<>(ci.getOperations());
            System.out.println(query);
            users = api.selectDatatypeWithOperation(tempDataTypes, tempOperations);
            tempDataTypes = new ArrayList<>(ci.getDataTypes());
            tempOperations = new ArrayList<>(ci.getOperations());
            if (testApi.testSelectedDatatypeWithOperation(tempDataTypes, tempOperations, users))
                System.out.println("Test Passed");
            else System.out.println("Test Failed");
            System.out.println("Number of users: " + users.size());
            System.out.println("Press 0 to go back to main menu");
            queryEnd = true;
            return false;
        }
        switch (i) {
            case 1 -> {
                if (!notUsed) {
                    System.out.println("NOT selected");
                    ci.addOperation(Operation.NOT);
                    this.notUsed = true;
                    return false;
                }
                System.out.println("NOT already used");
                return false;
            }
            case 2 -> {
                ci.changeState(ci.selectSexMenu);
                return true;
            }
            case 3 -> {
                ci.changeState(ci.selectAgeRangeMenu);
                return true;
            }
            case 4 -> {
                ci.changeState(ci.selectMemberClassMenu);
                return true;
            }
            case 5 -> {
                ci.abortAllOperations();
                ci.changeState(ci.mainMenu);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }
}
