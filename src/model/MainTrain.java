package model;

import java.util.Scanner;

public class MainTrain {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("What type of player are you? Enter: Host/Guest");
        String playerType = input.nextLine();
        System.out.println("Enter your name:"); //the player's name
        String name = input.nextLine();


        if (playerType == "Host"){
            HostModel hostPlayer = new HostModel();
            hostPlayer.name = name;
            hostPlayer.GameManagement();
        }
        else {
            GuestModel guestPlayer = new GuestModel();
            guestPlayer.name = name;
            guestPlayer.connectToServer();
            guestPlayer.startGame();
        }

    }
}
