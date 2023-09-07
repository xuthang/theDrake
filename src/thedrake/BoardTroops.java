package thedrake;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements JSONSerializable {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    //----------------------------------------------------------------------------

    public BoardTroops(PlayingSide playingSide) {
        this.playingSide = playingSide;
        this.troopMap = Collections.emptyMap();
        this.leaderPosition = TilePos.OFF_BOARD;
        this.guards = 0;

    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {

        this.playingSide = playingSide;
        this.troopMap = new HashMap<>(troopMap);
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    //----------------------------------------------------------------------------
    public Map<BoardPos, TroopTile> troopMap()
    {
        return troopMap;
    }

    public Optional<TroopTile> at(TilePos pos) {
        return Optional.ofNullable(troopMap.get(pos));
    }

    public PlayingSide playingSide() {
        return playingSide;
    }

    public TilePos leaderPosition() {
        return leaderPosition;
    }

    public int guards() {
        return guards;
    }

    //----------------------------------------------------------------------------

    public boolean isLeaderPlaced() {
        //return leaderPosition.getClass() != TilePos.OFF_BOARD.getClass();
        return at(leaderPosition).isPresent();
    }

    public boolean isPlacingGuards() {
        return isLeaderPlaced() && guards < 2;
    }

    public Set<BoardPos> troopPositions() {
        Set<BoardPos> ret = new HashSet<>();

        for (BoardPos i : troopMap.keySet()) {
            ret.add(i);
        }

        return ret;
    }

    //----------------------------------------------------------------------------

    public BoardTroops placeTroop(Troop troop, BoardPos target) {

        if (at(target).isPresent()) {
            throw new IllegalArgumentException("Cannot place piece on occupied place.");
        }

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.put(target, new TroopTile(troop, playingSide, TroopFace.AVERS));

        int newGuards = 0;

        return new BoardTroops(playingSide,
                newTroops,
                isLeaderPlaced() ? leaderPosition : target,
                isPlacingGuards() ? guards + 1 : guards);
    }

    //----------------------------------------------------------------------------

    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent()) {
            throw new IllegalArgumentException();
        }

        if (at(target).isPresent()) {
            throw new IllegalArgumentException("Cannot move to occupied place.");
        }

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(target, tile.flipped());

        return new BoardTroops(playingSide,
                newTroops,
                leaderPosition.equals(origin) ? target : leaderPosition,
                guards);

    }

    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot flip troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot flip troops before guards are placed.");
        }

        if (!at(origin).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot remove troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot remove troops before guards are placed.");
        }

        if (!at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.remove(target);

        return new BoardTroops(playingSide, newTroops,
                leaderPosition.equals(target) ? TilePos.OFF_BOARD : leaderPosition,
                guards);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.write("\"boardTroops\":{");
            writer.write("\"side\":\"" + playingSide + "\",");

            writer.write("\"leaderPosition\":\"");
            if (isLeaderPlaced())
                writer.write(leaderPosition.toString());
            else
                writer.write("off-board");
            writer.write("\",");

            writer.write("\"guards\":");
                writer.write("" + guards);
            writer.write(",");

            troopMapToJSON(writer);

        writer.write("}");
    }

    private void troopMapToJSON(PrintWriter writer)
    {
        writer.write("\"troopMap\":{");
        int Bcnt = 0;

        Comparator <BoardPos> comparator = Comparator.comparing(BoardPos::toString);
        Map<BoardPos, TroopTile> troopMapSorted = new TreeMap<BoardPos, TroopTile>(comparator);
        troopMapSorted.putAll(troopMap);

        for( Map.Entry<BoardPos, TroopTile> BTroop : troopMapSorted.entrySet() )
        {
            Bcnt++;
            writer.write( "\"" +  BTroop.getKey().toString() + "\":{");
            writer.write( "\"troop\":\"" +  BTroop.getValue().troop().name() + "\",");
            writer.write("\"side\":\"" + playingSide + "\",");
            writer.write("\"face\":\"" + BTroop.getValue().face() + "\"");
            writer.write("}");
            if(Bcnt != troopMapSorted.size())
                writer.write(",");
        }
        writer.write("}"); //end of troopMap
    }
}
