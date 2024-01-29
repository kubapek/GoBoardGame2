module com.goboardgame {
    requires javafx.controls;
    requires javafx.fxml;

    // Dodaj te linie dla Hibernate i JPA
    requires java.persistence;
    requires org.hibernate.orm.core;

    // Użyj opens, aby udostępnić Twoje pakiety do odpowiednich modułów
    opens com.goboardgame to javafx.fxml, org.hibernate.orm.core, java.persistence;

    exports com.goboardgame;
}
