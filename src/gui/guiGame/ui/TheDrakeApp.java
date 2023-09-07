package gui.guiGame.ui;

import gui.SceneSetter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import thedrake.Board;
import thedrake.BoardTile;
import thedrake.GameState;
import thedrake.PositionFactory;
import thedrake.StandardDrakeSetup;

public class TheDrakeApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startNewGame(primaryStage);
    }

    public void startSample(Stage primaryStage) throws Exception {
        BoardView boardView = new BoardView(createSampleGameState());
        primaryStage.setScene(new Scene(boardView.pane(), SceneSetter.width(), SceneSetter.height()));
        SceneSetter.setPrimaryStage(primaryStage);
        primaryStage.show();
    }

    private static GameState createSampleGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board)
                .placeFromStack(positionFactory.pos(0, 0))
                .placeFromStack(positionFactory.pos(3, 3))
                .placeFromStack(positionFactory.pos(0, 1))
                .placeFromStack(positionFactory.pos(3, 2))
                .placeFromStack(positionFactory.pos(1, 0))
                .placeFromStack(positionFactory.pos(2, 3));
    }

    public void startNewGame(Stage primaryStage) throws Exception{
        BoardView boardView = new BoardView(createNewGameState());
        SceneSetter.setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(boardView.pane(), SceneSetter.width(), SceneSetter.height()));
        primaryStage.show();
    }

    private static GameState createNewGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }

}
