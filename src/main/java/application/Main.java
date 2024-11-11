package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for launching the Mortgage Calculator application.
 * This class extends the JavaFX Application class to set up the primary stage and load the main view.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application by setting up the primary stage.
     * Loads the main view (HomeView.fxml) and sets the window title and dimensions.
     *
     * @param primaryStage the main stage for the application
     * @throws Exception if an error occurs during loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/fxml/HomeView.fxml"));
        primaryStage.setTitle("Mortgage Match");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();
    }

    /**
     * The main method, serving as the entry point for the application.
     * Calls the launch method to start the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
