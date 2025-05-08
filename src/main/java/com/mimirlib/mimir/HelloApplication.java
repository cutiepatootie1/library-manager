package com.mimirlib.mimir;

import com.mimirlib.mimir.Data.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/asset/icon.png")).toString());
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/mimirlib/mimir/maindih.fxml")));
        //scene customizations
        Scene scene = new Scene(root);
        stage.getIcons().add(icon);
        stage.setTitle("Mimir - the library manager");
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            DatabaseConnection dbCon = new DatabaseConnection();
            dbCon.Connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        launch(args);
    }
}