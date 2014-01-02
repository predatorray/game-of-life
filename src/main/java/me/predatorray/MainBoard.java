package me.predatorray;

public class MainBoard {

    private static final char ESC = 0x1B;
    private final String ANSI_CLS = "\u001b[2J";
    private final int row;
    private final int col;
    private final char dead;
    private final char alive;

    public MainBoard(int row, int col, char dead, char alive) {
        this.row = row;
        this.col = col;

        this.dead = dead;
        this.alive = alive;
    }

    public void clearScreen() {
        System.out.print(ANSI_CLS);
    }

    public void update(boolean[][] ceil) {
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                print(i, j, (ceil[i][j]) ? alive : dead);
            }
        }
    }

    private void print(int row, int col, char c) {
        System.out.print(String.format("%c[%d;%df%c", ESC, row, col, c));
    }
}
