module com.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.goboardgame to javafx.fxml;
    exports com.goboardgame;
}