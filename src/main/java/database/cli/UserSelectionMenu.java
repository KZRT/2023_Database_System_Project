package database.cli;

import database.api.User;

import java.util.ArrayList;

class UserSelectionMenu implements State {
    private ArrayList<User> users = new ArrayList<>();
    private ConsoleInterface ci;

    protected UserSelectionMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("User Selection Menu");
        System.out.println("Press Member Number of User to Select.");
        System.out.println("If user is not in the list, System will print \"User Not Found\" and return to this menu");
        System.out.println("If User is in the list, System will print \"User Found\" and user information, then return to this menu");
        System.out.println("Press 0 to go to User List Menu");
        System.out.println("Press -1 to return to main menu");
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public boolean nextInput(int i) {
        if (i > 0) {
            for (User user : users) {
                if (user.getMemberNumber() == i) {
                    System.out.println("User Found");
                    System.out.println(user.getUserInformation());
                    return false;
                }
            }
            System.out.println("User Not Found");
            return false;
        }
        if (i == 0) {
            ci.changeUserShowMenu(users);
            return true;
        }
        if (i == -1) {
            ci.changeState(ci.mainMenu);
            return true;
        } else return false;
    }
}
