package model;

import server.Board;
import server.Tile;
import server.Word;

public class EnterWord extends Event{

    Player player;
    String userWord;
    String fullUserWord;
    Word word;
    Tile[] word_tiles;
    int row;
    int col;
    boolean vertical;

    // server parameters

    boolean dic_ok;
    int score;
    Board tempBoard;






    EnterWord(Player player, String userWord, String row, String col, String vertical){
        this.event_id = 3;
        this.from_user = player.user_name;
        this.to_user = null;
        this.player = player;
        this.userWord = userWord;
        this.fullUserWord = userWord;
        word = null;
        word_tiles = null;
        this.row = Integer.parseInt(row);
        this.col = Integer.parseInt(col);
        if (vertical == "0")
            this.vertical = false;
        else this.vertical = true;

        this.dic_ok = false;
        this.score = 0;
        this.tempBoard = null;
    }


    //for testing a word in a Dictionary (query)
    public void createFullUserWord (){
        if (userWord.contains("_")) {
            for (int i = 0; i< userWord.length(); i++){
                if (userWord.charAt(i) == '_'){
                    if (vertical)
                        fullUserWord = fullUserWord.replace('_', BoardStatus.getBoardStatus().modelBoard.getTiles()[row+i][col].letter);
                    else
                        fullUserWord = fullUserWord.replace('_', BoardStatus.getBoardStatus().modelBoard.getTiles()[row][col+i].letter);
                }
            }
        }
    }


    //For testing a word in a Board (tryPlaceWord = knows how to take care of missing places)
    public void createWordObject(){
        word = new Word(helper(),row,col,vertical);
    }


    private Tile[] helper() {
        word_tiles= new Tile[userWord.length()];
        int i=0;
        for(char c: userWord.toCharArray()) {
            if(c == '_')
            {
                word_tiles[i] = null;
            }
            else {
                for(int j = 0; j < player.tiles.size(); j++)
                {
                    if(c == player.tiles.get(j).letter)
                    {
                        word_tiles[i] = player.tiles.remove(j); // take the tiles from the user
                        break;
                    }
                }
            }
            i++;
        }
        return word_tiles;
    }

    public void set_score(int score)
    {
        this.score = score;
    }

    public void set_dic(boolean ans)
    {
        this.dic_ok = ans;
    }

    public void set_Board(Board b)
    {
        BoardStatus updatedboard = new BoardStatus();
        updatedboard.modelBoard = b;

    }
}
