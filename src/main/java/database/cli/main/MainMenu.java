package database.cli;

public class MainMenu implements State {
    private ConsoleInterface ci;

    protected MainMenu(ConsoleInterface ci) {
        this.ci = ci;
    }
    @Override
    public void printScreen() {
        ci.triggerStateOccured = false;
        ci.countStateOccured = false;
        ci.abortAllOperations();
        System.out.println("Welcome to the Shopping Mall Database!");
        System.out.println("Please select an option:");
        System.out.println("1. Create Table");
        System.out.println("2. Insert Data");
        System.out.println("3. Create Bitmap Index");
        System.out.println("4. Select Datatype");
        System.out.println("5. Count Datatype");
        System.out.println("6. Select Datatype with Operation");
        System.out.println("7. Count Datatype with Operation");
        System.out.println("8. Test Tables");
        System.out.println("9. Exit");
    }

    @Override
    public boolean nextInput(int i) {
        switch (i){
            case 1:
                ci.changeState(ci.createTableMenu);
                return true;
            case 2:
                ci.changeState(ci.insertDataMenu);
                return true;
            case 3:
                ci.changeState(ci.createBitmapIndexMenu);
                return true;
            case 4:
                ci.changeState(ci.selectOneDatatypeMenu);
                return true;
            case 5:
                ci.countStateOccured = true;
                ci.changeState(ci.countOneDatatypeMenu);
                return true;
            case 6:
                ci.selectDatatypeWithOperationMenu.setTrigger(false);
                ci.changeState(ci.selectDatatypeWithOperationMenu);
                return true;
            case 7:
                ci.changeState(ci.countDatatypeWithOperationMenu);
                return true;
            case 8:
                ci.changeState(ci.testMenu);
                return true;
            case 9:
                System.exit(0);
        }
        return false;
    }
}
