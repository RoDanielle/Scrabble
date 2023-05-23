

/*
model:
case 0: s- 0|a,5^d,8^l,1^......
        c - gets seven tiles at the beginning of the game sends nothing back

case 1: c - 1|word(not full)|row|col|v/h|name for the word it wants to enter or 1|xxx|name if wants to pass

case 2:s - there are three options for response: a - query and try... were good: "2|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                                  b - only query was good: "2|true|0|name"
                                                  c - query returned false: "2|false|name"
       c - if query false challenge option presented:for challenge sends  3|c|word(not full)|row|col|v/h|name for passing sends 3|xxx|word(not full)|row|col|v/h|name to host

       *** in case 2 we get from host words that were entered and put onto the board by other users in order to put into other players board and maintain an updated board for all

case 3: s - challenge request answer, again three options: a - challenge and try... were good: "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                                                  b - only challenge was good: "3|true|0|name"
                                                  c - challenge returned false: "3|false|name"
 */


package model;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
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

        public void removeTiles(String word)
        {
            for(int i = 0; i < word.length(); i++) // remove the tiles used in the word that was put into the board
            {
                if(word.charAt(i) != '_')
                {
                    for(int j = 0; j < this.tiles.size(); j ++)
                    {
                        if(this.tiles.get(j).charAt(0) == word.charAt(i))
                        {
                            this.tiles.remove(j);
                            break;
                        }
                    }
                }
            }
        }

        public void addTiles(String addedTiles)
        {
            String[] moreTiles = addedTiles.split("^");
            for(String s : moreTiles)
            {
                if(s != "^")
                    this.tiles.add(s);
            }
        }

        public void updateMatrixBoard(String word, String row, String col, String vertical) {
            String tmpTileString = null;
            if(vertical == "v")
            {
                for(int i = 0; i < word.length(); i++)
                {
                    if(word.charAt(i) != '_')
                    {
                        tmpTileString = word.charAt(i) + "," + this.letterToScore.get(word.charAt(i));
                        this.board[Integer.parseInt(row) + i][Integer.parseInt(col)] = tmpTileString;
                    }
                }
            }
            else // "h"
            {
                for(int i = 0; i < word.length(); i++)
                {
                    if(word.charAt(i) != '_')
                    {
                        tmpTileString = word.charAt(i) + "," + this.letterToScore.get(word.charAt(i));
                        this.board[Integer.parseInt(row)][Integer.parseInt(col) + i] = tmpTileString;
                    }
                }
            }

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

        public void connectToServer(){
            String ip;
            int port;

            System.out.println("please enter server ip");
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            ip = in;
            System.out.println("please enter server port");
            in = input.nextLine();
            port = Integer.parseInt(in);

            try {
                Socket hostServer =new Socket(ip,port);
                System.out.println("Connection established successfully!");
                this.setMySocket(hostServer);
                this.startGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        public void startGame() {
            System.out.println("please enter your name");
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            this.name = in;

            System.out.println("please wait for Host to start the game");

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
                        System.out.println("game started, you got seven tiles: " + this.tiles); // will be deleted when we create view
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

                                this.removeTiles(fromHost[5]); // remove the tiles used in the word that was put into the board
                                this.addTiles(fromHost[3]); // add tiles received from the server replacing those used in the word add to board
                                System.out.println("your tiles are: " + this.tiles); // will be changed when view is added
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
                            if(fromHost[2] != "0") // challenge and tryplaceword correct, received: "3|true|score|a,1^b2^...|name|word(not full)|row|col|v\h"
                            {
                                int tmp_score = Integer.parseInt(fromHost[2]) + 10;
                                System.out.println( "your word was placed on the board, you get " + tmp_score + " points");
                                updateMatrixBoard(fromHost[5],fromHost[6],fromHost[7],fromHost[8]);
                                this.addScore(tmp_score);

                                this.removeTiles(fromHost[5]);
                                this.addTiles(fromHost[3]);
                                System.out.println("your tiles are: " + this.tiles); // will be changed when view is added
                            }
                            else // only challenge correct received: "3|true|0|name"
                            {
                                System.out.println("challenge returned true but word couldn't be put into board");
                                System.out.println("turn over");
                            }
                        }
                        else // challenge wasn't correct - deduce points received: "3|false|name"
                        {
                            System.out.println("challenge returned false, you lose 10 points");
                            this.score -= 10;
                            System.out.println("turn over");
                        }
                        break;
                }
            }
        }
    }
