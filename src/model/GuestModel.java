package model;

import java.util.List;

public class GuestModel implements GameModel{

    String name;
    int score;
    String[][] board;
    List<String> tiles;

    public GuestModel(){

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getWord() {
        return null;
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public void setScore(int score) {

    }

    @Override
    public void updateBoard(String word, int row, int col, boolean vertical) {

    }

    @Override
    public void startGame() {

    }

    public void connectToServer(String id, int port){

    }

}
