package model;

public class ModelBoard {
    private static ModelBoard _instance = null;
    private String[][] board;

    private ModelBoard() {
        board = new String[15][15];
    }


    // Public method to get the singleton instance
    public static ModelBoard getModelBoard() {
        if (_instance == null) {
            _instance = new ModelBoard();
        }
        return _instance;
    }


    // Getter method to access the matrix
    public String[][] getBoard() {
        return this.board;
    }

    // Setter method to update the matrix

    public void changeBoard(String word, int row, int col, boolean vertical) {

        if(vertical)
        {
            for(int i = 0 ; i < word.length(); i ++)
            {
                if(this.getBoard()[row + i][col] == null)
                this.getBoard()[row + i][col] = word.indent(i);
            }
        }

        else // horizontal
        {
            for(int i = 0; i < word.length(); i ++)
            {
                if(this.getBoard()[row][col + i] == null)
                    this.getBoard()[row][col + i] = word.indent(i);
            }
        }
    }
}
