package me.predatorray;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class GameOfLife {

    private static final int ROW = 24;
    private static final int COL = 80;
    private static final double DENSITY = 0.5;
    private static final long REFRESH_MILLIS = 500;
    private static final Random RANDOM = new Random();
    private static final ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());

    private static volatile boolean[][] ceilMap;

    public static void main(String[] args) throws InterruptedException,
            ExecutionException {
        ceilMap = new boolean[ROW][COL];
        MainBoard mainBoard = new MainBoard(ROW, COL, ' ', 'x');
        for (int i = 0; i < ROW; ++i) {
            for (int j = 0; j < COL; ++j) {
                ceilMap[i][j] = (RANDOM.nextDouble() >= DENSITY);
            }
        }

        mainBoard.clearScreen();
        CyclicBarrier barrier = new CyclicBarrier(2);
        new Thread(new RefreshCtrl(barrier, REFRESH_MILLIS)).start();
        new Thread(new CeilDisplay(barrier, mainBoard)).start();
    }

    private static class RefreshCtrl implements Runnable {

        private final CyclicBarrier barrier;
        private final long millis;

        public RefreshCtrl(CyclicBarrier barrier, long millis) {
            this.barrier = barrier;
            this.millis = millis;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(millis);
                    barrier.await();
                }
            } catch (Exception ignored) {}
        }
    }

    private static class CeilDisplay implements Runnable {

        private final CyclicBarrier barrier;
        private final MainBoard mainBoard;

        CeilDisplay(CyclicBarrier barrier, MainBoard mainBoard) {
            this.barrier = barrier;
            this.mainBoard = mainBoard;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    List<CeilAliveOrDeadTask> tasks = new
                            LinkedList<CeilAliveOrDeadTask>();
                    for (int i = 0; i < ROW; ++i) {
                        for (int j = 0; j < COL; ++j) {
                            boolean[] neighbour = getNeighbour(ceilMap, i, j);
                            tasks.add(new CeilAliveOrDeadTask(
                                    new Ceil(ceilMap[i][j], i,  j),
                                    neighbour));
                        }
                    }
                    List<Future<Ceil>> results =
                            EXECUTOR_SERVICE.invokeAll(tasks);
                    boolean[][] nextGen = new boolean[ROW][COL];
                    for (Future<Ceil> result : results) {
                        Ceil ceil = result.get();
                        nextGen[ceil.getRow()][ceil.getCol()] = ceil.isAlive();
                    }
                    mainBoard.update(nextGen);
                    ceilMap = nextGen;
                    barrier.await();
                }
            } catch (Exception ignored) {}
        }

        private int normalize(int n, int border) {
            int normalized = n % border;
            return (normalized >= 0) ? normalized : border + normalized;
        }

        private boolean[] getNeighbour(boolean[][] ceilMap, int row,
                                       int col) {
            return new boolean[] {
                    ceilMap[normalize(row - 1, ROW)][normalize(col - 1, COL)],
                    ceilMap[normalize(row - 1, ROW)][normalize(col, COL)],
                    ceilMap[normalize(row - 1, ROW)][normalize(col + 1, COL)],
                    ceilMap[normalize(row, ROW)][normalize(col - 1, COL)],
                    ceilMap[normalize(row, ROW)][normalize(col + 1, COL)],
                    ceilMap[normalize(row + 1, ROW)][normalize(col - 1, COL)],
                    ceilMap[normalize(row + 1, ROW)][normalize(col, COL)],
                    ceilMap[normalize(row + 1, ROW)][normalize(col + 1, COL)]
            };
        }
    }
}
