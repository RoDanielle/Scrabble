package model;

import java.util.Scanner;

public class MainTrain {

    public static void start(){
        Scanner input = new Scanner(System.in);
        System.out.println("What type of player are you? Enter 1 for Host, Enter 2 for Guest");
        String playerType = input.nextLine();

        if (playerType.equals("1")){
            System.out.println("you are now in Host mode");
            HostModel hostPlayer = new HostModel();
        }
        else {
            System.out.println("you are now in Guest mode");
            GuestModel guestPlayer = new GuestModel();
        }
    }

    public static void main(String[] args) {
        start();
        System.out.println("done");
    }
}
