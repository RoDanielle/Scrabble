package view;

import viewModel.ViewModel;

import java.util.Scanner;

public class View {
    String name;
    int score;
    String[][] board;
    ViewModel vm;


    public void start(){
        boolean ishost;
        boolean islocal;
        String num = "null";
        String ip = "null";
        String port = "null";
        String playerType;
        String gameType;

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter your name");
        this.name = input.nextLine();

        System.out.println("What type of player are you? Enter 1 for Host, Enter 2 for Guest");
        playerType = input.nextLine();

        if (playerType.equals("1")){ // HOST
            ishost = true;
            System.out.println("you are now in Host mode");
            System.out.println("Do you want to play a local game or host a remote game? for local enter: 1, for remote enter: 2");
            gameType = input.nextLine();
            System.out.println("How many player including you are playing? 1-4");
            num = input.nextLine();

            if(gameType.equals("1")) // LOCAL
            {
                islocal = true;

            }
            else { // REMOTE
                islocal = false;
            }
        }
        else { // GUEST
            ishost = false;
            islocal = false;
            System.out.println("you are now in Guest mode");
            System.out.println("Please enter server ip");
            ip = input.nextLine();
            System.out.println("Please enter server port");
            port = input.nextLine();

        }
        // TODO - create the viewmodel object after a startgame button was pressed
        vm = new ViewModel(name, ip, port, ishost, islocal, Integer.parseInt(num));
    }

}
