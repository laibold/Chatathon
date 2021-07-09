package de.hs_rm.chat_client;

import de.hs_rm.chat_client.communication.tcp.ServerMessageService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.model.tcp.message.InvalidHeaderException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Environment {
    private final Stage stage;
    private final ServerMessageService serverMessageService;

    public Environment(Stage stage) {
        this.stage = stage;
        this.serverMessageService = ServerMessageService.getInstance();

        stage.setOnCloseRequest(windowEvent -> {
            try {
                serverMessageService.sendSignOut();
            } catch (InvalidHeaderException | IOException ignore) {
            }
        });
    }

    public void startWith(BaseController baseController) {
        navigateTo(baseController);
    }

    public void navigateTo(BaseController controller) {
        try {
            controller.setEnvironment(this);

            var viewPath = "view" + File.separator + controller.getViewPath();
            var loader = new FXMLLoader(controller.getClass().getResource('/' + viewPath));
            loader.setController(controller);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopApp() {
        stage.close();
    }

}
