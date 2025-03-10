package com.mimirlib.mimir;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image("file:src/asset/icon.png");

        //group node shit
        Group root = new Group();
        //scene customizations
        Scene scene = new Scene(root, 900, 620, Color.rgb(40, 40, 40));
        stage.getIcons().add(icon);
        stage.setTitle("Mimir - the library manager");

        Text welcome = new Text();
        welcome.setText("A helpful assistant software that manages all your books");
        welcome.setX(50);
        welcome.setY(50);
        welcome.setFont(Font.font("Consolas", 25));
        welcome.setFill(Color.WHITE);

        root.getChildren().add(welcome);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}