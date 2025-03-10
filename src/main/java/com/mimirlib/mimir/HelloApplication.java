package com.mimirlib.mimir;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image("file:src/asset/icon.png");

        Group root = new Group();
        Scene scene = new Scene(root, Color.rgb(40, 40, 40));
        stage.getIcons().add(icon);
        stage.setTitle("Mimir - the library manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}