package model;

import java.io.InputStream;
import java.io.OutputStream;

public interface GameModel {

    public String getName();
    public int getScore();
    public void setScore(int score);
    public String[][] getBoard();

}
