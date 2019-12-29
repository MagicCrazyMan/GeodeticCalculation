module geodetic.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires geodetic.core;

    opens club.magiccrazyman.geodetic.gui to javafx.fxml;
    exports club.magiccrazyman.geodetic.gui;
}