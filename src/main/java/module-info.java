module com.mimirlib.mimir {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.mimirlib.mimir to javafx.fxml;
    exports com.mimirlib.mimir;
}