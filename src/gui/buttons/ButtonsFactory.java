package gui.buttons;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ButtonsFactory {

    public static Button quitButton(){
        Button quit = new Button("zpet");

        quit.setOnAction(value -> {
            try {
                new gui.guiStartPage.Main().start((Stage) quit.getScene().getWindow());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        quit.getStylesheets().add("/gui/buttons/styleSheet.css");
        return quit;
    }
}
