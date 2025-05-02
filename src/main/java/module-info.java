module com.mimirlib.mimir {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.xml.crypto;
    requires mysql.connector.j;


    opens com.mimirlib.mimir to javafx.fxml;
    exports com.mimirlib.mimir;
    exports com.mimirlib.mimir.Controller;
    opens com.mimirlib.mimir.Controller to javafx.fxml;
    opens com.mimirlib.mimir.Data to javafx.base;
}