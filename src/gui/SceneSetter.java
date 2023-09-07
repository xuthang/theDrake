package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SceneSetter {
    public static void setPane(GridPane pane){
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPadding(new Insets(15));
        pane.setAlignment(Pos.CENTER);
    }

    public static void setPrimaryStage(Stage primaryStage ){
        primaryStage.setMaxWidth(width());
        primaryStage.setMinWidth(width());

        primaryStage.setMaxHeight(height());
        primaryStage.setMinHeight(height());
    }

    public static int width(){return 1280;}
    public static int height(){return 720;}
}
