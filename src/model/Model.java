package model;

import java.io.*;
import java.net.Socket;
import java.util.*;





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


/*
model output to server by cases - string structure
1. ask for seven random tiles: "1|user_name"
2. word input and all related parameters: "2|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)" (query only)
3. challenge request (if query returns false and user wants to): "3|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)" (challenge only)






model input from server by cases - - string structure
1. server sent seven tiles: "1|user_name|ch,score^ch,score^ch,score^ch,score^ch,score^ch,score^ch,score^
2. server sends response to word query: "2|"true"/"false"|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)"
3. server sends response to word query: "3|"true"/"false"|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)"




 */



public class Model{
    //login
    //if(event1.tile == null) --> set
    //else-->get
    Player curr_player;
    List<Player> Q = new ArrayList<>();
    boolean gameOver = true;

    ModelBoard board;
    public Model(){
        board = ModelBoard.getModelBoard();
    }

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
                String answer_from_server = null;
                String request_to_server = null;
                ////////////////////////////////get 7 tiles started////////////////////////////////////
                if(Q.get(0).first_round){
                    request_to_server = "1" + "|" + Q.get(0).user_name;
                    write_to_server(request_to_server,socket);
                    answer_from_server = read_from_server(socket);
                    String[] outerArr = answer_from_server.split("|");
                    String[] tiles = outerArr[2].split("^");
                    //String[] newTiles = new String[tiles.length-1];
                    //for(int i=0; i<newTiles.length-1; i++){
                        //newTiles[i] = tiles[i];
                    //}
                    for(String s : tiles) {
                        Q.get(0).tiles.add(s);
                    }
                    Q.get(0).first_round = false;
                    Player p = Q.remove(0);
                    Q.add(p);
                    break;
                }
                ////////////////////////////////get 7 tiles finished////////////////////////////////////
                ////////////////////////////////enter word started////////////////////////////////////

                System.out.println("please enter Word");
                Scanner input = new Scanner(System.in);
                request_to_server = "2";

                String in = input.nextLine();
                request_to_server = request_to_server + "|" + fill_spaces(in) + "|" + in;
                System.out.println("Please enter row");
                in = input.nextLine();
                request_to_server = request_to_server + "|" + in;
                System.out.println("Please enter col");
                in = input.nextLine();
                request_to_server = request_to_server + "|" + in;
                System.out.println("Please enter v for vertical or h for horizontal");
                in = input.nextLine();
                request_to_server = request_to_server + "|" + in;
                request_to_server = request_to_server + Q.get(0).user_name;

                write_to_server(request_to_server,socket);

                answer_from_server = read_from_server(socket);


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //handle challenge
        //get response from server
        ////////////////////////////////enter word finished////////////////////////////////////


    }
    public String fill_spaces(String w){
        return "";
    }
    public void write_to_server(String str, Socket socket){
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(str);
            writer.flush();
            writer.close();
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