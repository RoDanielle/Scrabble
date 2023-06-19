package model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player {
    String name;
    public Socket socket;
    List<Tile> tiles;
    int numOfTiles;

    List<String> strTiles;

    int score;

    String[] wordDetails;

    public Player() {
        this.name = null;
        this.socket = null;
        this.tiles = new ArrayList<>();
        this.numOfTiles = this.tiles.size();
        this.score = 0;
        this.strTiles = new ArrayList<>();
        this.wordDetails = new String[4];
        this.wordDetails[0] = "null";
        this.wordDetails[1] = "null";
        this.wordDetails[2] = "null";
        this.wordDetails[3] = "null";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getScore()
    {
        return this.score;
    }
    public List<Tile> getTiles()
    {
        return this.tiles;
    }
    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }
    public int getNumOfTiles() {return this.numOfTiles;}
    public void addScore(int score)
    {
        this.score += score;
    }
    public void decreaseScore(int score)
    {
        this.score -= score;
    }

    /**
     * The create_word function takes in a string, row, column and vertical/horizontal
     * information. It then creates an array of tiles based on the input word. If there is
     * a blank tile in the input word, it will be represented by null in the array of tiles.
     * The function also removes these tiles from this player's hand so that they cannot be used again.

     *
     * @param input_word Create the word
     * @param _row Get the row of the word
     * @param _col Get the column of the word
     * @param _vertical Determine if the word is vertical or horizontal
     *
     * @return A word object
     *
     * @docauthor Trelent
     */
    public Word create_word(String input_word, String _row, String _col, String _vertical) {
        Tile[] wordarr = null;
        int row = Integer.parseInt(_row);
        int col = Integer.parseInt(_col);
        boolean vertical;
        if (_vertical.equals("vertical"))
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

    /**
     * The removeStrTiles function removes the tiles from the strTiles ArrayList that are used in a word.
     *
     *
     * @param word Determine which tiles to remove from the strtiles arraylist
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void removeStrTiles(String word) {
        for(int i = 0; i < word.length(); i++)
        {
            if(word.charAt(i) != '_')
            {
                for(int j = 0; j < this.strTiles.size(); j++)
                {
                    if(word.charAt(i) == this.strTiles.get(j).charAt(0))
                    {
                        this.strTiles.remove(j);
                        break;
                    }
                }
            }
        }
    }
}

