package me.predatorray;

public class Ceil {

    private final int row;
    private final int col;
    private final boolean alive;

    public Ceil(boolean alive, int row, int col) {
        this.alive = alive;
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isAlive() {
        return alive;
    }
}
