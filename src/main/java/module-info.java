module com.mimirlib.mimir {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.xml.crypto;


    opens com.mimirlib.mimir to javafx.fxml;
    exports com.mimirlib.mimir;
    exports com.mimirlib.mimir.Controller;
    opens com.mimirlib.mimir.Controller to javafx.fxml;
}