package model;

public interface GameModel {

    public String getName();
    public String getWord();
    public int getScore();
    public void setScore(int score);
    public void updateBoard (String word, int row, int col, boolean vertical);
    public String[][] getBoard();
    public void startGame ();
}
