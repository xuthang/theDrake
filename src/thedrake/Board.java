package thedrake;

import java.io.PrintWriter;

public class Board implements JSONSerializable {

    private final int dimension;
    private final BoardTile[][] tiles;

    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
    public Board(int dimension) {
        this.dimension = dimension;
        tiles = new BoardTile[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                tiles[i][j] = BoardTile.EMPTY;
            }
        }
    }

    // Rozměr hrací desky
    public int dimension() {
        return this.dimension;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(BoardPos pos) {
        return tiles[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {
        Board ret = new Board(this.dimension);

        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                ret.tiles[i][j] = this.tiles[i][j];
            }
        }

        for (TileAt i : ats) {
            ret.tiles[i.pos.i()][i.pos.j()] = i.tile;
        }

        return ret;
    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        return new PositionFactory(this.dimension);
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.write("\"board\":{");
            writer.write("\"dimension\":" + dimension() + ",");

            writer.write("\"tiles\":[");

                for(int i = 0; i < dimension() ; i++) {
                    for(int j = 0; j < dimension() ; j++) {
                        if(tiles[j][i] == BoardTile.MOUNTAIN) {
                            writer.write("\"mountain\"");
                        }
                        else if(tiles[j][i] == BoardTile.EMPTY){
                            writer.write("\"empty\"");
                        }
                        if( i + 1< tiles.length || (i + 1 == dimension() && j + 1 < dimension()))
                            writer.write(",");
                    }
                }
            writer.write("]");

        writer.write("}");

    }
}

