package com.mimirlib.mimir;

import com.mimirlib.mimir.Controller.BookController;
import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image("file:src/asset/icon.png");
        //group node shit
       //Group root = new Group();
        Parent root = FXMLLoader.load(getClass().getResource("maindih.fxml"));
        //scene customizations
        Scene scene = new Scene(root);
        stage.getIcons().add(icon);
        stage.setTitle("Mimir - the library manager");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        DatabaseConnection dbCon = new DatabaseConnection();
        dbCon.Connect();
        launch(args);
    }
}