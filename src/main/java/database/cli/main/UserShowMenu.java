package database.cli;

import database.api.User;

import java.util.ArrayList;

class UserShowMenu implements State{
    private ArrayList<User> users = new ArrayList<>();
    private ConsoleInterface ci;
    private long userIndex = 0;

    protected UserShowMenu(ConsoleInterface ci) {
        this.ci = ci;
    }

    @Override
    public void printScreen() {
        System.out.println("User Showing Menu");
        System.out.println("Press Number of Users to Show, Users are sorted in Member ID order");
        System.out.println("Press 0 to go to User Selection Menu");
        System.out.println("Press -1 to return to main menu");
    }

    public void setUsers(ArrayList<User> users){
        this.users = users;
    }

    @Override
    public boolean nextInput(int i) {
        if(i > 0){
            System.out.println("Showing " + (userIndex + 1) + "~"+ (userIndex + i) + " Users");
            for(int j = 0; j < i; j++){
                if(userIndex + j < users.size()){
                    System.out.println(users.get((int) (userIndex + j)).getMemberNumber());
                }
            }
            userIndex += i;
            if (userIndex >= users.size()){
                System.out.println("All Users Shown, Returning to Start");
                userIndex = 0;
            }
            return false;
        }
        if(i == 0){
            ci.changeUserSelectionMenu(users);
            return true;
        }
        if(i == -1){
            ci.changeState(ci.mainMenu);
            return true;
        }
        else return false;
    }
}
