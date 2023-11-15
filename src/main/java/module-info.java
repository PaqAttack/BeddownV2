module com.paqattack.gui_template {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.paqattack.gui_template to javafx.fxml;
    exports com.paqattack.gui_template;
}