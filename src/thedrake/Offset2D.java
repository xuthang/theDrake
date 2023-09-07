package thedrake;

public class Offset2D {
    public final int x;
    public final int y;


    public boolean equalsTo(int x, int y) {
        if (this.x != x)
            return false;

        if (this.y != y)
            return false;

        return true;
    }

    public Offset2D yFlipped() {
        return new Offset2D(x, -y);
    }

    public Offset2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
}



