package database.cli;

import database.api.Operation;
import database.api.Sex;

class SelectOperationMenu implements State {
    private ConsoleInterface ci;

    protected SelectOperationMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Selecting Operation");
        System.out.println("Please select an option:");
        System.out.println("1. AND");
        System.out.println("2. OR");
        System.out.println("3. End Query");
        System.out.println("4. Abort All Operations");
    }

    @Override
    public boolean nextInput(int i) {
        switch (i) {
            case 1 -> {
                ci.addOperation(Operation.AND);
                if (ci.countStateOccured) ci.changeState(ci.countDatatypeWithOperationMenu, false);
                else ci.changeState(ci.selectDatatypeWithOperationMenu, false);
                return true;
            }
            case 2 -> {
                ci.addOperation(Operation.OR);
                if (ci.countStateOccured) ci.changeState(ci.countDatatypeWithOperationMenu, false);
                else ci.changeState(ci.selectDatatypeWithOperationMenu, false);
                return true;
            }
            case 3 -> {
                if (ci.countStateOccured) ci.changeState(ci.countDatatypeWithOperationMenu, true);
                else ci.changeState(ci.selectDatatypeWithOperationMenu, true);
                return true;
            }
            case 4 -> {
                ci.abortAllOperations();
                ci.changeState(ci.mainMenu);
                return true;
            }
        }
        return false;
    }
}
