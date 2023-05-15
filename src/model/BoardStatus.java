package model;
import test.Board;
import java.util.Observable;
import java.util.Observer;


public class BoardStatus extends Observable {

    private static BoardStatus _instance = null;
    Board modelBoard;
    //need to add observers list (viewModel?)


    public BoardStatus(){
        modelBoard = Board.getBoard();
    } //TODO - neeed to fix cant  call getboard

    public static BoardStatus getBoardStatus( ) {
        if(_instance == null)
        {
            _instance = new BoardStatus();
        }
        return _instance;
    }

    public void addObserver(Observer observer){
        //need to implement when decided
    }

    public void update(Board newBoard){
        this.modelBoard = newBoard;
        this.setChanged();
        this.notifyObservers();
    }
}
