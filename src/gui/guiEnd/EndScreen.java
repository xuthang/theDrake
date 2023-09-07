package gui.guiEnd;

import gui.SceneSetter;
import gui.buttons.ButtonsFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import thedrake.PlayingSide;

import javax.security.auth.callback.LanguageCallback;
import java.util.Set;

public class EndScreen extends Application {

    private String winner = "DRAW";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(setPane(), SceneSetter.width(), SceneSetter.height()));
        primaryStage.show();
    }

    public void setWinner(PlayingSide side){
        this.winner = (side == PlayingSide.BLUE ? "Blue" : "Orange") + " player won";
    }

    private GridPane setPane(){
        GridPane pane = new GridPane();
        SceneSetter.setPane(pane);
        setText(pane);
        setButton(pane);
        return pane;
    }

    private void setText(GridPane pane){
        GridPane text = new GridPane();
        SceneSetter.setPane(text);
        Label winnerText = new Label(winner);
        winnerText.setStyle("-fx-font-size:  " + 30 + "; -fx-text-alignment: center;");
        text.add(winnerText, 0, 0);
        pane.add(text, 0, 0);
    }

    private void setButton(GridPane pane){
        GridPane button = new GridPane();
        SceneSetter.setPane(button);
        button.add(ButtonsFactory.quitButton(), 0, 0);
        pane.add(button, 0, 1);
    }
}
