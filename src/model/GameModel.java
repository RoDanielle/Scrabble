package model;

import java.util.List;
import java.util.Observer;

public interface GameModel {
    public String getName();
    public int getScore();
    public String[][] getBoard();
    public List<String> getTiles();
    public String getMessage();
    public void addObserver(Observer obz);
    public void setUserQueryInput(String word, String row, String col, String vertical);
    public void setUserChallengeInput(String request);
}
