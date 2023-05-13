package model;

import test.Tile;
import test.Word;

public class EnterWord extends Event{

    Player player;
    String userWord;
    String fullUserWord;
    Word word;
    Tile[] t;
    int row;
    int col;
    boolean vertical;

    EnterWord(Player player, String userWord, String row, String col, String vertical){
        this.event_id = 3;
        this.from_user = player.user_name;
        this.to_user = null;
        this.player = player;
        this.userWord = userWord;
        this.fullUserWord = userWord;
        word = null;
        t = null;
        this.row = Integer.parseInt(row);
        this.col = Integer.parseInt(col);
        if (vertical == "0")
            this.vertical = false;
        else this.vertical = true;
    }


    //for testing a word in a Dictionary (query)
    public void createFullUserWord (){
        if (userWord.contains("_")) {
            for (int i = 0; i< userWord.length(); i++){
                if (userWord.charAt(i) == '_'){
                    if (vertical)
                        fullUserWord = fullUserWord.replace('-', BoardStatus.getBoardStatus().modelBoard.getTiles()[row+i][col].letter);
                    else
                        fullUserWord = fullUserWord.replace('-', BoardStatus.getBoardStatus().modelBoard.getTiles()[row][col+i].letter);
                }
            }
        }
    }


    //For testing a word in a Board (tryPlaceWord = knows how to take care of missing places)
    public void createWordObject(){
        word = new Word(helper(),row,col,vertical);
    }


    private Tile[] helper() {
        t= new Tile[userWord.length()];
        int i=0;
        for(char c: userWord.toCharArray()) {
            t[i]= Tile.Bag.getBag().getTile(c);
            i++;
        }
        return t;
    }
}
