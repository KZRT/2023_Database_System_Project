package database.cli;

import database.api.AgeRange;

class SelectAgeRangeMenu implements State {
    private ConsoleInterface ci;

    protected SelectAgeRangeMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Selecting Age Range");
        System.out.println("Please select an option:");
        System.out.println("1. Babies (0 - 9)");
        System.out.println("2. Teen (10 - 19)");
        System.out.println("3. Twenties (20 - 29)");
        System.out.println("4. Thirties (30 - 39)");
        System.out.println("5. Forties (40 - 49)");
        System.out.println("6. Fifties (50 - 59)");
        System.out.println("7. Sixties (60 - 69)");
        System.out.println("8. Seventies (70 - 79)");
        System.out.println("9. Eighties (80 - 89)");
        System.out.println("10. Nineties (90 - 99)");
        System.out.println("11. Over Hundreds (100 - 32767)");
        System.out.println("12. Return to Previous Menu");
        System.out.println("13. Abort All Operations");
    }

    @Override
    public boolean nextInput(int i) {
        switch (i) {
            case 1:
                ci.addDataType(AgeRange.BABIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 2:
                ci.addDataType(AgeRange.TEEN);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 3:
                ci.addDataType(AgeRange.TWENTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 4:
                ci.addDataType(AgeRange.THIRTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 5:
                ci.addDataType(AgeRange.FORTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 6:
                ci.addDataType(AgeRange.FIFTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 7:
                ci.addDataType(AgeRange.SIXTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 8:
                ci.addDataType(AgeRange.SEVENTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 9:
                ci.addDataType(AgeRange.EIGHTIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 10:
                ci.addDataType(AgeRange.NINETIES);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 11:
                ci.addDataType(AgeRange.OVER_HUNDREDS);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 12:
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu, false);
                    else ci.changeState(ci.selectOneDatatypeMenu, false);
                } else {
                    if (ci.countStateOccured) ci.changeState(ci.countDatatypeWithOperationMenu, false);
                    else ci.changeState(ci.selectDatatypeWithOperationMenu, false);
                }
                return true;
            case 13:
                ci.abortAllOperations();
                ci.changeState(ci.mainMenu);
                return true;
        }
        return false;
    }
}
