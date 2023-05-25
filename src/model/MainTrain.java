package model;

import server.BookScrabbleHandler;
import server.MyServer;

import java.util.Random;
import java.util.Scanner;

public class MainTrain {

    public static void start(){
        Scanner input = new Scanner(System.in);
        System.out.println("What type of player are you? Enter: Host/Guest");
        String playerType = input.nextLine();

        if (playerType == "Host"){
            HostModel hostPlayer = new HostModel();
            Random r=new Random();
            int gameServerPort=6000+r.nextInt(1000);
            MyServer s=new MyServer(gameServerPort, new BookScrabbleHandler());
            s.start();
            hostPlayer.GameManagement("localhost", gameServerPort);
        }
        else {
            GuestModel guestPlayer = new GuestModel();
            guestPlayer.connectToServer();
        }
    }


    public static void main(String[] args) {
        start();
    }
}


