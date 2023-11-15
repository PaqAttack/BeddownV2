module com.paqattack.gui_template {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;

    exports com.paqattack.gui_template to javafx.graphics;
    opens com.paqattack.gui_template to javafx.fxml;
    exports com.paqattack.gui_template.windows;
    opens com.paqattack.gui_template.windows to javafx.fxml;
}