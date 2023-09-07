package gui.guiStartPage;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import gui.guiGame.ui.TheDrakeApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Controller {

    public Button twoPlayers;
    public Button exit;
    public Button byInternet;
    public Button againstComputer;

    @FXML
    private void closeWindow(){
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void startNewGame() {
        TheDrakeApp theDrakeApp = new TheDrakeApp();
        try {
            theDrakeApp.startNewGame((Stage) byInternet.getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startSampleGame() {
        TheDrakeApp theDrakeApp = new TheDrakeApp();
        try {
            theDrakeApp.startSample((Stage) byInternet.getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
