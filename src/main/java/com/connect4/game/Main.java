package com.connect4.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("connect4.fxml"));
        GridPane rootGridPane = fxmlLoader.load();
        primaryStage.setTitle("Connect 4");

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        controller = fxmlLoader.getController();
        controller.createPlayground();

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu() {
        Menu fileMenu = new Menu("File");
        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(event -> resetGame());

        MenuItem resetGameMenuItem = new MenuItem("Reset Game");
        resetGameMenuItem.setOnAction(event -> resetGame());

        SeparatorMenuItem separatorOne = new SeparatorMenuItem();
        MenuItem quitGame = new MenuItem("Quit Game");
        quitGame.setOnAction(event -> quit());

        fileMenu.getItems().addAll(newGameMenuItem, resetGameMenuItem, separatorOne, quitGame);

        Menu helpMenu = new Menu("Help");

        MenuItem aboutGameMenu = new MenuItem("About Game");
        aboutGameMenu.setOnAction(event -> aboutGame());

        SeparatorMenuItem separatorTwo = new SeparatorMenuItem();

        MenuItem aboutMeMenu = new MenuItem("About me");
        aboutMeMenu.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGameMenu, separatorTwo, aboutMeMenu);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert aboutMe = new Alert(Alert.AlertType.INFORMATION);
        aboutMe.setTitle("About The Developer");
        aboutMe.setHeaderText("Piyush Sonkamble");
        aboutMe.setContentText("""
                Hi there,\s
                I am a beginner for developing games\s
                but soon I will become a pro in game development.
                Enjoy playing the game!!""".indent(1));
        aboutMe.show();
    }

    private void aboutGame() {
        Alert aboutGame = new Alert(Alert.AlertType.INFORMATION);
        aboutGame.setTitle("About Connect4");
        aboutGame.setHeaderText("How to Play?");
        aboutGame.setContentText("Connect Four is a two-player connection game in which the\nplayers first choose a color and then take turns dropping \ncolored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down,\noccupying the next available space within the column.\nThe objective of the game is to be the first to form a horizontal,\nvertical, or diagonal line of four of one's own discs.\nConnect Four is a solved game.\nThe first player can always win by playing the right moves.");
        aboutGame.show();
    }

    private void quit() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        controller.resetGame();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
