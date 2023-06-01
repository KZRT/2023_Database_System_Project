package database.cli;

import database.api.*;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleInterface {
    private static ConsoleInterface instance = null;
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
    protected State testMenu;
    protected State currentState;
    protected State selectSexMenu;
    protected State selectAgeRangeMenu;
    protected State selectMemberClassMenu;
    protected State selectOperationMenu;
    protected State userShowMenu;
    protected State userSelectionMenu;

    private ConsoleInterface() {

        this.dataTypes = new ArrayList<>();
        this.operations = new ArrayList<>();
        this.mainMenu = new MainMenu(this);
        this.createTableMenu = new CreateTableMenu(this);
        this.insertDataMenu = new InsertDataMenu(this);
        this.createBitmapIndexMenu = new CreateBitmapIndexMenu(this);
        this.countOneDatatypeMenu = new CountOneDatatypeMenu(this);
        this.selectOneDatatypeMenu = new SelectOneDatatypeMenu(this);
        this.selectSexMenu = new SelectSexMenu(this);
        this.selectAgeRangeMenu = new SelectAgeRangeMenu(this);
        this.selectMemberClassMenu = new SelectMemberClassMenu(this);
        this.selectDatatypeWithOperationMenu = new SelectDatatypeWithOperationMenu(this);
        this.countDatatypeWithOperationMenu = new CountDatatypeWithOperationMenu(this);
        this.selectOperationMenu = new SelectOperationMenu(this);
        this.userShowMenu = new UserShowMenu(this);
        this.userSelectionMenu = new UserSelectionMenu(this);
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

    protected void changeUserShowMenu(ArrayList<User> users) {
        ((UserShowMenu) userShowMenu).setUsers(users);
        changeState(userShowMenu);
    }

    protected void changeUserSelectionMenu(ArrayList<User> users) {
        ((UserSelectionMenu) userSelectionMenu).setUsers(users);
        changeState(userSelectionMenu);
    }

    protected void close() {
        this.currentState = null;
    }


    public static ConsoleInterface getInstance() {
        if (instance == null) {
            instance = new ConsoleInterface();
        }
        return instance;
    }
}
