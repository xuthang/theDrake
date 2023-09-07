package gui.guiStartPage;

import gui.SceneSetter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Display.fxml"));
        primaryStage.setTitle("The Drake");
        SceneSetter.setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(root, SceneSetter.width(), SceneSetter.height()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
