package me.predatorray;

import java.util.concurrent.Callable;

public class CeilAliveOrDeadTask implements Callable<Ceil> {

    private final Ceil previous;
    private final boolean[] neighbourStatus;

    public CeilAliveOrDeadTask(Ceil previous, boolean[] neighbourStatus) {
        this.previous = previous;
        this.neighbourStatus = neighbourStatus;
    }

    @Override
    public Ceil call() throws Exception {
        int aliveCount = 0;
        for (boolean alive : neighbourStatus) {
            if (alive) {
                ++aliveCount;
            }
        }

        if (!previous.isAlive()) {
            if (aliveCount == 3) {
                return new Ceil(true, previous.getRow(), previous.getCol());
            } else {
                return previous;
            }
        } else {
            if (aliveCount < 2) {
                return new Ceil(false, previous.getRow(), previous.getCol());
            } else if (aliveCount == 2 || aliveCount == 3) {
                return previous;
            } else {
                return new Ceil(false, previous.getRow(), previous.getCol());
            }
        }
    }
}
