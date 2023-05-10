package test;
import java.io.Serializable;


public class BoardStatus implements Serializable{

    private static BoardStatus _instance = null;
    Board board;
    int score;


    public BoardStatus(){
        board = Board.getBoard();
        this.score = 0;
    }

    public static BoardStatus getBoardStatus( ) {
        if(_instance == null)
        {
            _instance = new BoardStatus();
        }
        return _instance;
    }
}
