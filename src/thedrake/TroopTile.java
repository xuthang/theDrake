package thedrake;

import java.util.ArrayList;
import java.util.List;

public final class TroopTile implements Tile {

    private Troop troop;
    private PlayingSide side;
    private TroopFace face;

    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<Move> result = new ArrayList<>();
        List<TroopAction> actions =  troop.actions(face);

        for ( TroopAction i: actions) {
            result.addAll( i.movesFrom(pos, side, state));
        }

        return result;
    }

    // color of player side
    public PlayingSide side() {
        return side;
    }

    // face of troop on tile
    public TroopFace face() {
        return face;
    }

    // troop that stands on tile
    public Troop troop() {
        return troop;
    }

    // cant step in this tile, occupied already
    public boolean canStepOn() {
        return false;
    }

    public boolean hasTroop() {
        return true;
    }

    //returns tile with flipped troop
    public TroopTile flipped() {
        return new TroopTile(troop, side, (face == TroopFace.AVERS ? TroopFace.REVERS : TroopFace.AVERS));
    }
}
