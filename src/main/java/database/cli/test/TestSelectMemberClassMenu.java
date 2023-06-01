package database.cli.main;

import database.api.MemberClass;

class SelectMemberClassMenu implements State {
    private ConsoleInterface ci;

    protected SelectMemberClassMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("Selecting Age Range");
        System.out.println("Please select an option:");
//        BRONZE(0L, 10000L), SILVER(10000L, 50000L), GOLD(50000L, 100000L), PLATINUM(100000L, 500000L),
//                DIAMOND(500000L, 1000000L), VIP(1000000L, Integer.MAX_VALUE * 2L);
        System.out.println("1. Bronze (0 - 10000)");
        System.out.println("2. Silver (10000 - 50000)");
        System.out.println("3. Gold (50000 - 100000)");
        System.out.println("4. Platinum (100000 - 500000)");
        System.out.println("5. Diamond (500000 - 1000000)");
        System.out.println("6. VIP (1000000 - )");
        System.out.println("7. Return to Previous Menu");
        System.out.println("8. Abort All Operations");
    }

    @Override
    public boolean nextInput(int i) {
        switch (i) {
            case 1:
                ci.addDataType(MemberClass.BRONZE);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 2:
                ci.addDataType(MemberClass.SILVER);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 3:
                ci.addDataType(MemberClass.GOLD);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 4:
                ci.addDataType(MemberClass.PLATINUM);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 5:
                ci.addDataType(MemberClass.DIAMOND);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 6:
                ci.addDataType(MemberClass.VIP);
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu);
                    else ci.changeState(ci.selectOneDatatypeMenu);
                } else {
                    ci.changeState(ci.selectOperationMenu);
                }
                return true;
            case 7:
                if (!ci.multiDatatypeStateOccured) {
                    if (ci.countStateOccured) ci.changeState(ci.countOneDatatypeMenu, false);
                    else ci.changeState(ci.selectOneDatatypeMenu, false);
                } else {
                    if (ci.countStateOccured) ci.changeState(ci.countDatatypeWithOperationMenu, false);
                    else ci.changeState(ci.selectDatatypeWithOperationMenu, false);
                }
                return true;
            case 8:
                ci.abortAllOperations();
                ci.changeState(ci.mainMenu);
                return true;
        }
        return false;
    }
}
