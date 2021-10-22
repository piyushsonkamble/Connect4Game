package com.connect4.game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discCOLOR1 = "#24303E";
    private static final String discCOLOR2 = "#4CAA88";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;
    private boolean isAllowedToInsert = true;
    private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];


    @FXML
    public GridPane rootPane;
    @FXML
    public Pane menuPane;
    @FXML
    public Pane insertedDiscPane;
    @FXML
    public TextField playerOneTextField;
    @FXML
    public TextField playerTwoTextField;
    @FXML
    public VBox statusPane;
    @FXML
    public Button setNamesButton;
    @FXML
    public Label currentPlayerName;

    public  void createPlayground() {

        Shape rectangleWithHoles = gameGrid();
        rootPane.add(rectangleWithHoles,0,1);
        List<Rectangle> rectangleList = createClickableColumns();
        currentPlayerName.setText(PLAYER_ONE);
        for (Rectangle rectangle: rectangleList){
            rootPane.add(rectangle, 0, 1);
        }
        setNamesButton.setOnAction(event -> {
            if(playerOneTextField.getText().length() == 0)
                PLAYER_ONE = "Player One";
            else
                PLAYER_ONE = playerOneTextField.getText();
            if(playerTwoTextField.getText().length() == 0)
                PLAYER_TWO = "Player Two";
            else
                PLAYER_TWO = playerTwoTextField.getText();

            currentPlayerName.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });
    }

    private Shape gameGrid(){
        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
        for(int row = 0; row <ROWS; row++) {
            for(int col = 0; col<COLUMNS; col++) {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2.0);
                circle.setCenterX((CIRCLE_DIAMETER / 2.0));
                circle.setCenterY((CIRCLE_DIAMETER / 2.0));
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4.0));
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4.0));

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumns(){
        List<Rectangle> rectangleList = new ArrayList<>();

        for( int col = 0; col<COLUMNS; col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4.0));

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert = false;
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });
            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column){

        int row = ROWS - 1;

        while (row >= 0){
            if(getDiscIfPresent(row, column) == null)
                break;
            row--;
        }

        if(row < 0)
            return;

        insertedDiscsArray[row][column] = disc;
        insertedDiscPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4.0));
        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + (CIRCLE_DIAMETER / 4.0));
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true;
            if(gameEnded(currentRow, column) || isGridFull()){
                gameOver();
                return;
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            currentPlayerName.setText(isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO);
        });
        translateTransition.play();
    }

    private void gameOver() {
        if(isGridFull()){
            Alert gameDraw = new Alert(Alert.AlertType.INFORMATION);
            gameDraw.setTitle("Connect Four");
            gameDraw.setHeaderText("Game Draw !!");
            gameDraw.setContentText("Want to play again?");

            ButtonType yesBtn = new ButtonType("Yes");
            ButtonType noBtn = new ButtonType("No, Exit");
            gameDraw.getButtonTypes().setAll(yesBtn,noBtn);

            Platform.runLater(() -> {
                Optional<ButtonType> btnClicked = gameDraw.showAndWait();
                if(btnClicked.isPresent() && btnClicked.get() == yesBtn){
                    resetGame();
                }else{
                    Platform.exit();
                    System.exit(0);
                }
            });

        }else {
            String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
            winnerDialogue(winner);
        }
    }

    private void winnerDialogue(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("Winner is "+ winner);
        alert.setContentText("Want to play again?");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn,noBtn);

        Platform.runLater(() -> {
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get() == yesBtn){
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertedDiscPane.getChildren().clear();
        for(int row = 0; row < insertedDiscsArray.length; row++){
            for(int col = 0; col < insertedDiscsArray[row].length; col++){
                insertedDiscsArray[row][col] = null;

            }
        }
        isPlayerOneTurn = true;
        PLAYER_ONE = "Player One";
        PLAYER_TWO = "Player Two";
        currentPlayerName.setText(PLAYER_ONE);
        playerOneTextField.clear();
        playerTwoTextField.clear();

        createPlayground();
    }

    private boolean gameEnded(int row, int column) {
        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3, row + 3).mapToObj(r -> new Point2D(r, column)).collect(Collectors.toList());
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3, column + 3).mapToObj(col -> new Point2D(row, col)).collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
                .mapToObj(i ->startPoint1.add(i,-i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i ->startPoint2.add(i,i))
                .collect(Collectors.toList());

        return  checkCombinations(verticalPoints)
                || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points)
                || checkCombinations(diagonal2Points);

    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain =0;
        for (Point2D point: points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();
            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
            if(disc!= null && disc.isPlayerOneMove == isPlayerOneTurn){
                chain++;
                if(chain == 4){
                    return true;
                }
            }else{
                chain = 0;
            }
        }
        return false;
    }

    private Disc getDiscIfPresent(int row, int column){
        if(row >= ROWS || row < 0 || column >= COLUMNS || column < 0){
            return null;
        }
        return insertedDiscsArray[row][column];
    }
    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            setRadius(CIRCLE_DIAMETER / 2.0);
            this.isPlayerOneMove = isPlayerOneMove;
            setFill(isPlayerOneMove? Color.valueOf(discCOLOR1): Color.valueOf(discCOLOR2));
            setCenterX(CIRCLE_DIAMETER / 2.0);
            setCenterY(CIRCLE_DIAMETER / 2.0);
        }
    }

    private boolean isGridFull(){
        boolean flag = true;

        int row = 0;// row < insertedDiscsArray.length; row++){
        for(int col = 0; col < insertedDiscsArray[row].length; col++){
            if(insertedDiscsArray[row][col] == null){
                flag = false ;
                break;
            }
        }
        return flag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
