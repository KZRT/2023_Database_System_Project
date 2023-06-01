package database.cli.test;

import database.api.Sex;

class TestSelectSexMenu implements State {
    private TestConsoleInterface ci;

    protected TestSelectSexMenu(TestConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Selecting Sex");
        System.out.println("Please select an option:");
        System.out.println("1. Not Known");
        System.out.println("2. Male");
        System.out.println("3. Female");
        System.out.println("4. Not Applicable");
        System.out.println("5. Return to Previous Menu");
        System.out.println("6. Abort All Operations");
    }

    @Override
    public boolean nextInput(int i) {
        switch (i) {
            case 1:
                ci.addDataType(Sex.NOT_KNOWN);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 2:
                ci.addDataType(Sex.MALE);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 3:
                ci.addDataType(Sex.FEMALE);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 4:
                ci.addDataType(Sex.NOT_APPLICABLE);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 5:
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu, false);
                    else ci.changeState(ci.selectOneDatatypeMenu, false);
                } else {
                    if (ci.countStateOccured) ci.changeState(ci.countDatatypeWithOperationMenu, false);
                    else ci.changeState(ci.selectDatatypeWithOperationMenu, false);
                }
                return true;
            case 6:
                ci.abortAllOperations();
                ci.changeState(ci.mainMenu);
                return true;
        }
        return false;
    }
}
