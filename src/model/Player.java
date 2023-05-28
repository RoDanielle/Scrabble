package model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player {
    String name;
    public Socket socket;
    List<Tile> tiles;

    int score;

    public Player() {
        this.name = null;
        this.socket = null;
        this.tiles = new ArrayList<>();
        this.score = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getScore()
    {
        return this.score;
    }

    public void addScore(int score)
    {
        this.score += score;
    }
    public void decreaseScore(int score)
    {
        this.score -= score;
    }

    public Word create_word(String input_word, String _row, String _col, String _vertical) {
        Tile[] wordarr = null;
        int row = Integer.parseInt(_row);
        int col = Integer.parseInt(_col);
        boolean vertical;
        if (_vertical.equals("V") || _vertical.equals("v"))
            vertical = true;
        else
            vertical = false;

        wordarr = new Tile[input_word.length()];
        int i = 0;
        for (char c : input_word.toCharArray()) {
            if (c == '_') {
                wordarr[i] = null;
            } else {
                for (int j = 0; j < this.tiles.size(); j++) {
                    if (c == this.tiles.get(j).letter) {
                        wordarr[i] = this.tiles.remove(j); // take the tiles
                        break;
                    }
                }
            }
            i++;
        }

        Word word = new Word(wordarr, row, col, vertical);
        return word;
    }
}