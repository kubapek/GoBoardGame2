module com.goboardgame.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.goboardgame.goboardgame to javafx.fxml;
    exports com.goboardgame.goboardgame;
}