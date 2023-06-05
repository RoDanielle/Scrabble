package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import viewModel.MainViewModel;

public class MainViewController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label boardLabel;
    private MainViewModel viewModel;
    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void start() {
        nameLabel.textProperty().bind(viewModel.nameProperty());
        scoreLabel.textProperty().bindBidirectional(viewModel.scoreProperty(), new NumberStringConverter());
        boardLabel.textProperty().bindBidirectional(viewModel.boardProperty(), new BoardStringConverter());
    }


    private static class BoardStringConverter extends StringConverter<String[][]> {
        @Override
        public String toString(String[][] board) {
            // Convert the board to a string representation
            // You can implement custom logic here based on your board structure
            StringBuilder builder = new StringBuilder();
            for (String[] row : board) {
                for (String cell : row) {
                    builder.append(cell).append(" ");
                }
                builder.append("\n");
            }
            return builder.toString();
        }

        @Override
        public String[][] fromString(String string) {
            // This conversion is not used for binding from view to model
            // You can leave it empty or throw an exception if not applicable
            return null;
        }
    }
}