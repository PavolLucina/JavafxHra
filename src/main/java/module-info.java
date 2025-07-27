module com.example.projektsjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projektsjavafx to javafx.fxml;
    exports com.example.projektsjavafx;
}