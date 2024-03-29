package database.cli.test;

import database.api.DataType;
import database.api.Operation;
import database.api.User;

import java.util.ArrayList;
import java.util.Scanner;

public class TestConsoleInterface {
    private static TestConsoleInterface instance = null;
    private final ArrayList<DataType> dataTypes;
    private final ArrayList<Operation> operations;
    protected boolean triggerStateOccured = false;
    protected boolean multiDatatypeStateOccured = false;
    protected boolean countStateOccured = false;
    protected State mainMenu;
    protected State createTableMenu;
    protected State insertDataMenu;
    protected State createBitmapIndexMenu;
    protected TriggerState selectOneDatatypeMenu;
    protected TriggerState countOneDatatypeMenu;
    protected TriggerState selectDatatypeWithOperationMenu;
    protected TriggerState countDatatypeWithOperationMenu;
    protected State currentState;
    protected State selectSexMenu;
    protected State selectAgeRangeMenu;
    protected State selectMemberClassMenu;
    protected State selectOperationMenu;

    public TestConsoleInterface() {
        this.dataTypes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.mainMenu = new TestMenu(this);
        this.createTableMenu = new TestCreateTableMenu(this);
        this.insertDataMenu = new TestInsertDataMenu(this);
        this.createBitmapIndexMenu = new TestCreateBitmapIndexMenu(this);
        this.countOneDatatypeMenu = new TestCountOneDatatypeMenu(this);
        this.selectOneDatatypeMenu = new TestSelectOneDatatypeMenu(this);
        this.selectSexMenu = new TestSelectSexMenu(this);
        this.selectAgeRangeMenu = new TestSelectAgeRangeMenu(this);
        this.selectMemberClassMenu = new TestSelectMemberClassMenu(this);
        this.selectDatatypeWithOperationMenu = new TestSelectDatatypeWithOperationMenu(this);
        this.countDatatypeWithOperationMenu = new TestCountDatatypeWithOperationMenu(this);
        this.selectOperationMenu = new TestSelectOperationMenu(this);
        changeState(mainMenu);
    }

    protected ArrayList<DataType> getDataTypes() {
        return dataTypes;
    }

    protected ArrayList<Operation> getOperations() {
        return operations;
    }

    protected DataType getDataType(int i) {
        return dataTypes.get(i);
    }

    protected Operation getOperation(int i) {
        return operations.get(i);
    }

    protected void addDataType(DataType dataType) {
        dataTypes.add(dataType);
    }

    protected void addOperation(Operation operation) {
        operations.add(operation);
    }

    protected void abortAllOperations() {
        dataTypes.clear();
        operations.clear();
    }

    protected void changeState(State state) {
        this.currentState = state;
        state.printScreen();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int i = scanner.nextInt();
            scanner.nextLine();
            if (state.nextInput(i)) {
                return;
            }
        }
    }

    protected void changeState(TriggerState state) {
        Scanner scanner = new Scanner(System.in);
        if (triggerStateOccured) {
            state.setTrigger(true);
            this.triggerStateOccured = false;
            this.currentState = state;
            state.nextInput(3);
            while (true) {
                int i = scanner.nextInt();
                scanner.nextLine();
                if (state.nextInput(i)) {
                    return;
                }
            }
        } else {
            triggerStateOccured = true;
            changeState((State) state);
        }
    }

    protected void changeState(TriggerState state, boolean triggered) {
        if (triggered) changeState(state);
        else changeState((State) state);
    }

    public static TestConsoleInterface getInstance() {
        if (instance == null) {
            instance = new TestConsoleInterface();
        }
        return instance;
    }
}
