module com.connect4.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.connect4.game to javafx.fxml;
    exports com.connect4.game;
}