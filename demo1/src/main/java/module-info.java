module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.graphics;
    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
    requires cloudinary.core;

}