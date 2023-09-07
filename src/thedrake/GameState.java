package thedrake;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameState implements JSONSerializable{
	private final Board board;
	private final PlayingSide sideOnTurn;
	private final Army blueArmy;
	private final Army orangeArmy;
	private final GameResult result;
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy) {
		this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
	}
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy, 
			PlayingSide sideOnTurn, 
			GameResult result) {
		this.board = board;
		this.sideOnTurn = sideOnTurn;
		this.blueArmy = blueArmy;
		this.orangeArmy = orangeArmy;
		this.result = result;
	}
	
	public Board board() {
		return board;
	}
	
	public PlayingSide sideOnTurn() {
		return sideOnTurn;
	}
	
	public GameResult result() {
		return result;
	}
	
	public Army army(PlayingSide side) {
		if(side == PlayingSide.BLUE) {
			return blueArmy;
		}
		
		return orangeArmy;
	}
	
	public Army armyOnTurn() {
		return army(sideOnTurn);
	}
	
	public Army armyNotOnTurn() {
		if(sideOnTurn == PlayingSide.BLUE)
			return orangeArmy;
		
		return blueArmy;
	}
	
	public Tile tileAt(BoardPos pos) {
		Optional<TroopTile> ret = armyOnTurn().boardTroops().at(pos);
		if( ret.isPresent())
			return ret.get();

		ret = armyNotOnTurn().boardTroops().at(pos);
		if(ret.isPresent())
			return ret.get();

		return board.at(pos);
	}
	
	private boolean canStepFrom(TilePos origin) {
		// Vrátí true, pokud je možné ze zadané pozice začít tah nějakou
		// jednotkou. Vrací false, pokud stav hry není IN_PLAY, pokud
		// na dané pozici nestojí žádné jednotka nebo pokud na pozici
		// stojí jednotka hráče, který zrovna není na tahu.
		// Při implementaci vemte v úvahu zahájení hry. Dokud nejsou
		// postaveny stráže, žádné pohyby jednotek po desce nejsou možné.
		if(result != GameResult.IN_PLAY)
			return false;

		if( ! (origin instanceof BoardPos ))
			return false;

		if(armyNotOnTurn().boardTroops().at(origin).isPresent())
			return false;

		if(armyOnTurn().boardTroops().at(origin).isEmpty())
			return false;

		if(!armyOnTurn().boardTroops().isLeaderPlaced() )
			return false;

		if(armyOnTurn().boardTroops().isPlacingGuards() )
			return false;

		return true;
	}

	private boolean canStepTo(TilePos target) {
		if(result != GameResult.IN_PLAY)
			return false;

		if( ! (target instanceof BoardPos ))
			return false;

		if( ! tileAt((BoardPos)target).canStepOn() )
			return false;

		if(armyNotOnTurn().boardTroops().at(target).isPresent() || armyOnTurn().boardTroops().at(target).isPresent())
			return false;

		if(! armyOnTurn().boardTroops().isLeaderPlaced() )
			return false;

		if(armyOnTurn().boardTroops().isPlacingGuards() )
			return false;

		return true;
	}
	
	private boolean canCaptureOn(TilePos target) {
		if(result != GameResult.IN_PLAY)
			return false;

		if(target == TilePos.OFF_BOARD)
			return false;

		if(armyNotOnTurn().boardTroops().at(target).isEmpty() || armyOnTurn().boardTroops().at(target).isPresent())
			return false;

		if(!armyNotOnTurn().boardTroops().isLeaderPlaced() || !armyOnTurn().boardTroops().isLeaderPlaced() )
			return false;

		if(armyNotOnTurn().boardTroops().isPlacingGuards() || armyOnTurn().boardTroops().isPlacingGuards() )
			return false;

		return true;
	}
	
	public boolean canStep(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canStepTo(target);
	}
	
	public boolean canCapture(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canCaptureOn(target);
	}
	
	public boolean canPlaceFromStack(TilePos target) {


		if(result != GameResult.IN_PLAY)
			return false;

		if(armyOnTurn().stack().isEmpty() )
			return false;

		if( ! (target instanceof BoardPos ))
			return false;

		if(!board().at((BoardPos)target).canStepOn())
			return false;

		if(!armyOnTurn().boardTroops().isLeaderPlaced() ) {
			if(sideOnTurn() == PlayingSide.BLUE)
				return target.row() == 1;
			else
				return target.row() == board.dimension();
		}

		if(armyNotOnTurn().boardTroops().at(target).isPresent() || armyOnTurn().boardTroops().at(target).isPresent())
			return false;

		if(armyOnTurn().boardTroops().isPlacingGuards()) {
			return armyOnTurn().boardTroops().leaderPosition().isNextTo(target);
		}

		List<BoardPos> n = ((BoardPos) target).neighbours();
		for (BoardPos i : n) {
			if (armyOnTurn().boardTroops().at(i).isPresent())
				return true;
		}

		return false;
	}
	
	public GameState stepOnly(BoardPos origin, BoardPos target) {		
		if(canStep(origin, target))		 
			return createNewGameState(
					armyNotOnTurn(),
					armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);
		
		throw new IllegalArgumentException();
	}
	
	public GameState stepAndCapture(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target), 
					armyOnTurn().troopStep(origin, target).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState captureOnly(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target),
					armyOnTurn().troopFlip(origin).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState placeFromStack(BoardPos target) {
		if(canPlaceFromStack(target)) {
			return createNewGameState(
					armyNotOnTurn(), 
					armyOnTurn().placeFromStack(target), 
					GameResult.IN_PLAY);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState resign() {
		return createNewGameState(
				armyNotOnTurn(), 
				armyOnTurn(), 
				GameResult.VICTORY);
	}
	
	public GameState draw() {
		return createNewGameState(
				armyOnTurn(), 
				armyNotOnTurn(), 
				GameResult.DRAW);
	}
	
	private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
		if(armyOnTurn.side() == PlayingSide.BLUE) {
			return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
		}
		
		return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result); 
	}

	@Override
	public void toJSON(PrintWriter writer) {
	    writer.write("{");
	        result.toJSON(writer);
	        writer.write(",");

	        board.toJSON(writer);
	        writer.write(",");

            writer.write("\"blueArmy\":{");
                blueArmy.toJSON(writer);
            writer.write("},");

            writer.write("\"orangeArmy\":{");
                orangeArmy.toJSON(writer);
            writer.write("}");

        writer.print("}");
	}
}
