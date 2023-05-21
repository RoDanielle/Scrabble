package model;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static java.lang.String.valueOf;

    public class GuestModel implements GameModel{

        String name;
        int score;
        String[][] board;
        List<String> tiles;
        boolean gameRunning;

        Map<String,String> letterToScore;

        Socket mySocket;

        public GuestModel(){
            this.name = null;
            this.score = 0;
            this.board = new String[15][15];
            this.tiles = new ArrayList<>();
            this.gameRunning = true;
            this.mySocket = null;
            this.letterToScore = null;
            this.letterToScore = new HashMap<>();
            letterToScore.put("A","1");
            letterToScore.put("B","3");
            letterToScore.put("C","3");
            letterToScore.put("D","2");
            letterToScore.put("E","1");
            letterToScore.put("F","4");
            letterToScore.put("G","2");
            letterToScore.put("H","4");
            letterToScore.put("I","1");
            letterToScore.put("J","8");
            letterToScore.put("K","5");
            letterToScore.put("L","1");
            letterToScore.put("M","3");
            letterToScore.put("N","1");
            letterToScore.put("O","1");
            letterToScore.put("P","3");
            letterToScore.put("Q","10");
            letterToScore.put("R","1");
            letterToScore.put("S","1");
            letterToScore.put("T","1");
            letterToScore.put("U","1");
            letterToScore.put("V","4");
            letterToScore.put("W","4");
            letterToScore.put("X","8");
            letterToScore.put("Y","4");
            letterToScore.put("Z","10");
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getScore() {
            return this.score;
        }

        @Override
        public void addScore(int score) { // adding scoring
            this.score += score;
        }

        public Socket getMySocket() {
            return this.mySocket;
        }

        public void setMySocket(Socket socket) {
            this.mySocket = socket;
        }


        public void connectToServer(){
            String ip;
            int port;

            System.out.println("please server ip");
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            ip = in;
            System.out.println("please server port");
            in = input.nextLine();
            port = Integer.parseInt(in);

            try {
                Socket hostServer =new Socket(ip,port);
                this.setMySocket(hostServer);
                starGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        public void starGame() {
            System.out.println("please enter your name");
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            this.name = in;


            while (gameRunning){
                // reading from server
                String[] fromHost = this.read_from_server(this.getMySocket()).split("|");
                String request_to_server = null;
                switch (fromHost[0]){
                    case "0": // seven tiles at the start of the game
                        String[] tilesArray = fromHost[1].split("^");
                        for(int i = 0; i < tilesArray.length; i++) {
                            this.tiles.add(tilesArray[i]);
                        }
                        break;
                    case "1": // my turn
                        System.out.println("please enter Word, if you don't have any word enter xxx");
                        in = input.nextLine();
                        request_to_server = "1";
                        request_to_server = request_to_server + "|" + in;
                        if(in != "xxx")
                        {
                            System.out.println("Please enter row");
                            in = input.nextLine();
                            request_to_server = request_to_server + "|" + in;
                            System.out.println("Please enter col");
                            in = input.nextLine();
                            request_to_server = request_to_server + "|" + in;
                            System.out.println("Please enter v for vertical or h for horizontal");
                            in = input.nextLine();
                            request_to_server = request_to_server + "|" + in;
                        }

                        request_to_server = request_to_server + "|" + this.getName();
                        write_to_server(request_to_server,this.getMySocket());
                        break;//send: 1|word(not full)|row|col|v/h|name or 1|xxx|name

                    case "2": // host + server response to query request ++ updated board for any entered word
                        if(fromHost[1] == "true" && fromHost[4] != this.name) // other users word was placed on board
                        {
                            updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
                            System.out.println(fromHost[4] + "placed a new word on the board");
                        }

                        else if(fromHost[1] == "true" && fromHost[4] == this.name) // received:"2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                        {
                            if(fromHost[3] != "0") // my word was put into board
                            {
                                System.out.println( "your word was placed on the board, you get " + fromHost[3] + " points");
                                updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
                                this.addScore(Integer.parseInt(fromHost[2]));
                                // TODO - take the words tiles from user and give it the new ones
                            }
                            else //"2|true|0|name"
                                System.out.println("your word wouldn't be fit into the board");
                                System.out.println("turn over");

                        }
                        else // my query request returned false received:"2|false|name"
                        {
                            System.out.println("couldn't find your word in dictionary");
                            System.out.println("for challenge enter c for passing your turn enter xxx");
                            in = input.nextLine();
                            String[] temp_request = request_to_server.split("|");
                            request_to_server = "3" + "|" + in + "|" + temp_request[1] + temp_request[2] + temp_request[3] + temp_request[4] +temp_request[5];
                            write_to_server(request_to_server,this.getMySocket());

                        }
                        break; // if user wants to challenge or not, send: 3|c/xxx|word(not full)|row|col|v/h|name

                    case "3": // host + server response to challenge request
                        if(fromHost[1] == "true")
                        {
                            if(fromHost[2] != "0") // challenge and tryplaceword correct received: "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                            {
                                int tmp_score = Integer.parseInt(fromHost[2]) + 10;
                                System.out.println( "your word was placed on the board, you get " + tmp_score + " points");
                                updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
                                this.addScore(tmp_score);
                                // TODO - take the words tiles from user and give it the new ones

                            }
                            else // only challenge correct received: "3|true|0|name" // TODO - check if now we also give bonus
                            {
                                System.out.println("challenge returned true but word couldn't be put into board");
                                System.out.println("turn over");
                            }
                        }
                        else // challenge wasn't correct - deduce points received: "3|false|name"
                        {
                            System.out.println("challenge returned false, you loose 10 points");
                            this.score -= 10;
                            System.out.println("turn over");
                        }
                        break;
                }
            }
        }


        public void updateMatrixBoard(String word, String row, String col, String vertical) {

        }

        @Override
        // Getter method to access the matrix
        public String[][] getBoard() {
            return this.board;
        }

        public void write_to_server(String str, Socket server_socket){
            try {
                PrintWriter outToServer = new PrintWriter(server_socket.getOutputStream());
                outToServer.println(str);
                outToServer.flush();
                outToServer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        public String read_from_server(Socket server_socket) {
            try {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
                String serverResponse = inFromServer.readLine();
                inFromServer.close();
                return serverResponse;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }




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



        //login
        //if(event1.tile == null) --> set
        //else-->get

    /*
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
                // delete first tile from the players




                while(gameRunning) {
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

                    answer_from_server = read_from_server(socket); //TODO handel response from server to the requested word (query at this point)


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


    }

    */

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