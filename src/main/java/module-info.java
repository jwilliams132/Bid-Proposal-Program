module gearworks {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
	requires javafx.graphics;

    opens gearworks to javafx.fxml;

    exports gearworks;
}
