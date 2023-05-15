package model;

/*
menu-model:
1. asks for one random tile.
2. sends back the rand tiles used for setting turns.
3. gets word input from client, check for pass, check if ok, create word obj, send word to server.
4. reads response from server and updated users score and all users boards.

menu-server:
1. call getTile func one time and gives it to model.
2. puts back the tiles received into the bag.
3. server - checks words and sends response to the word. if correct sends board and score. if not sends false.
*/

import test.Tile;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Model{
    //login
    //if(event1.tile == null) --> set
    //else-->get
    Player curr_player;
    List<Player> Q = new ArrayList<>();
    boolean gameOver = true;
    public Model(){}
    public void start(){
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println("start game");
            ArrayList<Integer> abc = new ArrayList<>();
            for(int i=1; i<=26; i++){
                abc.add(i);
            }
            for(int i=0; i<Q.size(); i++){
                Random rand = new Random();
                int low = 1;
                int high = abc.size();
                int index = rand.nextInt(high-low)+low;
                int res = abc.get(index);
                abc.remove(index);
                Q.get(i).tiles.add(res+"");
            }

            Q.sort((a,b)->Integer.parseInt(a.tiles.get(0)) - Integer.parseInt(b.tiles.get(0)));

            while(!gameOver) {
                System.out.println("please enter Word");
                Scanner input = new Scanner(System.in);
                String getWord = "1";

                String in = input.nextLine();
                getWord = getWord + "|" + fill_spaces(in) + "|" + in;
                System.out.println("Please enter row");
                in = input.nextLine();
                getWord = getWord + "|" + in;
                System.out.println("Please enter col");
                in = input.nextLine();
                getWord = getWord + "|" + in;
                System.out.println("Please enter v for vertical or h for horizontal");
                in = input.nextLine();
                getWord = getWord + "|" + in;
                getWord = getWord + Q.get(0).user_name;

                write_to_server(getWord,socket);
                String server_ans = read_from_server(socket);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
    public String fill_spaces(String w){
        return "";
    }
    public void write_to_server(String str, Socket socket){
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.close();
        writer.println(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public String read_from_server(Socket socket) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverResponse = reader.readLine();
            reader.close();
            return serverResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}



 /*
        System.out.println("Welcome! for host - press 1, for guest - press 2");
        Scanner input = new Scanner(System.in);
        String in = input.nextLine();
        if(in.equals("1"))
        {
            //start server
            //create Player and add to Q.
        }
        else
        {
            //connect to server
            //create Player and add to Q.
        }
         */

///////////////////////all users are connected, ready to start game//////////////////////////
    /*
        RandTile RT = new RandTile(curr_user);
        Socket socket = new Socket("localhost", 12345);
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        outputStream.writeObject(RT);
        objectOutputStream.flush();
        objectOutputStream.close();
        outputStream.close();
    */