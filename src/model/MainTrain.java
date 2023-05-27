package model;

import server.BookScrabbleHandler;
import server.MyServer;
import test.GuestModelTest;
import test.HostModelTest;
import java.util.Random;
import java.util.Scanner;


public class MainTrain {

    public static void start(){
        Boolean flag = false;
        Scanner input = new Scanner(System.in);
        String playerType =  null;

        while (!flag) {
            System.out.println("What type of player are you? Enter: Host/Guest");
            playerType = input.nextLine();
            if (playerType.equals("Host") || playerType.equals("host")) {
                flag = true;
                System.out.println("You are now in Host mode");
                HostModel hostPlayer = new HostModel();
                Random r = new Random();
                int gameServerPort = 6000 + r.nextInt(1000);
                System.out.println("main - game server port is: " + gameServerPort);
                MyServer s = new MyServer(gameServerPort, new BookScrabbleHandler());
                s.start();
                hostPlayer.GameManagement("localhost", gameServerPort);
                s.close();
                break;
            }
            if (playerType.equals("Guest") || playerType.equals("guest")) {
                flag = true;
                System.out.println("You are now in Guest mode");
                GuestModel guestPlayer = new GuestModel();
                guestPlayer.connectToServer();
                break;
            }
            System.out.println("Error! try again");
        }
    }


    public static void test(){
        HostModelTest.start();
        //GuestModelTest.start();
    }

        public static void main(String[] args) {
        //start();
        //System.out.println("done");

        //Tests for the programmers
        test();
        System.out.println("done");
    }
}


