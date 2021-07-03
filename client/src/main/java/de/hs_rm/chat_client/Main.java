package de.hs_rm.chat_client;

import de.hs_rm.chat_client.controller.SignUpController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RatChat");
        primaryStage.minWidthProperty().setValue(300);
        primaryStage.minHeightProperty().setValue(500);

        var environment = new Environment(primaryStage);
        environment.startWith(new SignUpController());
    }

    public static void main(String[] args) {
        launch();
    }
}
