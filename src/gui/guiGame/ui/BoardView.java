package gui.guiGame.ui;

import java.util.List;
import java.util.stream.Collectors;

import gui.SceneSetter;
import gui.buttons.ButtonsFactory;
import gui.guiEnd.EndScreen;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import thedrake.*;

public class BoardView extends GridPane implements TileViewContext {

    private GameState gameState;

    private ValidMoves validMoves;

    private TileView selected;

    private GridPane pane = new GridPane();

    private ListView<String> blueStack = new ListView<String>();
    private ListView<String> blueCaptured = new ListView<String>();
    private ListView<String> orangeStack = new ListView<String>();
    private ListView<String> orangeCaptured = new ListView<String>();

    public BoardView(GameState gameState) {

        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);

        SceneSetter.setPane(this.pane);
        setInfo();

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                BoardPos boardPos = positionFactory.pos(x, 3 - y);
                add(new TileView(boardPos, gameState.tileAt(boardPos), this), x, y);
            }
        }

        SceneSetter.setPane(this);

        updateListViews();
    }

    public GridPane pane() {
        return this.pane;
    }

    private void setInfo() {
        setLabel(blueStack);
        setLabel(blueCaptured);
        setLabel(orangeStack);
        setLabel(orangeCaptured);

        initBoxes();
        initButtons();

        pane.add(this, 2, 1, 1, 3);
    }

    private static void setLabel(ListView<String> label) {
        label.setOrientation(Orientation.VERTICAL);
        label.setMouseTransparent(true);
        label.setFocusTraversable(false);
    }

    private static void setBox(VBox box, Label side, ListView<String> stack) {
        box.setSpacing(10);
        box.setMaxWidth(180);
        box.getChildren().addAll(side, stack);
    }

    private void initBoxes() {
        VBox blueStackBox = new VBox();
        setBox(blueStackBox, new Label("Blue Stack: "), blueStack);
        pane.add(blueStackBox, 1, 1);

        VBox blueCapturedBox = new VBox();
        setBox(blueCapturedBox, new Label("Blue Captured: "), blueCaptured);
        pane.add(blueCapturedBox, 1, 2);

        VBox orangeStackBox = new VBox();
        setBox(orangeStackBox, new Label("Orange Stack: "), orangeStack);
        pane.add(orangeStackBox, 3, 1);

        VBox orangeCapturedBox = new VBox();
        setBox(orangeCapturedBox, new Label("Orange Captured: "), orangeCaptured);
        pane.add(orangeCapturedBox, 3, 2);
    }


    private void initButtons() {
        GridPane buttonPane = new GridPane();

        Button quit = ButtonsFactory.quitButton();

        SceneSetter.setPane(buttonPane);
        buttonPane.add(quit, 0, 0);
        pane.add(buttonPane, 2, 0);
    }

    private void updateListViews() {
        blueStack.getItems().clear();
        blueStack.getItems().addAll(gameState.army(PlayingSide.BLUE).stack().stream().map(Troop::name).collect(Collectors.toList()));

        blueCaptured.getItems().clear();
        blueCaptured.getItems().addAll(gameState.army(PlayingSide.BLUE).captured().stream().map(Troop::name).collect(Collectors.toList()));

        orangeStack.getItems().clear();
        orangeStack.getItems().addAll(gameState.army(PlayingSide.ORANGE).stack().stream().map(Troop::name).collect(Collectors.toList()));

        orangeCaptured.getItems().clear();
        orangeCaptured.getItems().addAll(gameState.army(PlayingSide.ORANGE).captured().stream().map(Troop::name).collect(Collectors.toList()));
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView)
            selected.unselect();

        selected = tileView;

        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));
    }

    @Override
    public void executeMove(Move move) {
        selected.unselect();
        selected = null;
        clearMoves();
        gameState = move.execute(gameState);
        validMoves = new ValidMoves(gameState);
        updateTiles();
    }

    private void updateTiles() {
        if (isEnd()) {
            EndScreen End = new gui.guiEnd.EndScreen();

            if(gameState.result() == GameResult.VICTORY){
                End.setWinner(gameState.armyNotOnTurn().side());
            }

            try {
                End.start((Stage) this.getScene().getWindow());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.setTile(gameState.tileAt(tileView.position()));
            tileView.update();
        }

        updateListViews();

    }

    private void clearMoves() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private void showMoves(List<Move> moveList) {
        for (Move move : moveList)
            tileViewAt(move.target()).setMove(move);
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (3 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);
    }

    private boolean isEnd() {
        if (gameState.result() == GameResult.VICTORY) {
            return true;
        }
        else if (validMoves.allMoves().isEmpty() && gameState.result() != GameResult.VICTORY) {
            gameState = gameState.draw();
            return true;
        }
        return false;
    }
}
