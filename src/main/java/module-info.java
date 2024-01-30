module com.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;

    // Dodaj te linie dla Hibernate i JPA
    requires java.persistence;
    requires org.hibernate.orm.core;

    // Użyj opens, aby udostępnić Twoje pakiety do odpowiednich modułów
    opens com.goboardgame to javafx.fxml, org.hibernate.orm.core, java.persistence;

    exports com.goboardgame;
    exports com.goboardgame.dto;
    opens com.goboardgame.dto to java.persistence, javafx.fxml, org.hibernate.orm.core;
    exports com.goboardgame.server;
    opens com.goboardgame.server to java.persistence, javafx.fxml, org.hibernate.orm.core;
    exports com.goboardgame.client;
    opens com.goboardgame.client to java.persistence, javafx.fxml, org.hibernate.orm.core;
}
