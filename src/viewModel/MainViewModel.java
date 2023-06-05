package viewModel;

import javafx.beans.property.*;


public class MainViewModel {
    private final StringProperty nameProperty;
    private final IntegerProperty scoreProperty;
    private final ObjectProperty<String[][]> boardProperty;

    public MainViewModel() {
        nameProperty = new SimpleStringProperty();
        scoreProperty = new SimpleIntegerProperty();
        boardProperty = new SimpleObjectProperty<>();
    }

    // Getter for the name property
    public StringProperty nameProperty() {
        return nameProperty;
    }

    // Getter for the name value
    public String getName() {
        return nameProperty.get();
    }

    // Setter for the name value
    public void setName(String name) {
        nameProperty.set(name);
    }


    // Getter for the score property
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    // Getter for the score value
    public int getScore() {
        return scoreProperty.get();
    }

    // Setter for the score value
    public void setScore(int score) {
        scoreProperty.set(score);
    }


    public ObjectProperty<String[][]> boardProperty() {
        return boardProperty;
    }

    public void setBoard(String[][] board) {
        boardProperty.set(board);
    }

    public String[][] getBoard() {
        return boardProperty.get();
    }
}