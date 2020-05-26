package ai;

import gamelogic.*;

import java.lang.annotation.ElementType;
import java.util.Arrays;

/**
 * Created by Bouke Willem on 1-2-2015.
 */
public class MonteCarloStrategy implements Strategy {
    private long allowedTime;
    private long startTime;
    int[] wins;
    int[] loss;
    float[] ratio;

    public MonteCarloStrategy(int time) {
        allowedTime = (long) time * 1000;
    }

    @Override
    public String getName() {
        return "Monte Carlo";
    }

    @Override
    public int determineMove(Board b, Mark m) {
        startTime = System.currentTimeMillis();
        wins = new int[7];
        loss = new int[7];
        ratio = new float[7];
        think(b, m);
        float value = 0;
        int highest = 0;
        for (int i = 0; i < Board.HOR; i++) {
            if (loss[i] != 0) {
                ratio[i] = wins[i] / loss[i];
            } else {
                ratio[i] = wins[i];
            }
            if (ratio[i] > value) {
                value = ratio[i];
                highest = i;
            }
        }
        return highest;
    }

    private void think(Board b, Mark m) {
        long currentTime = System.currentTimeMillis();
        while ((currentTime - startTime) < allowedTime) {
            for (int i = 0; i < Board.HOR; i++) {
                Board bn = b.deepCopy();
                if (b.colHasSpace(i)) {
                    bn.setInCol(i, m);
                    playGame(bn, m, i);
                }
            }
            currentTime = System.currentTimeMillis();
        }
    }

    private void playGame(Board bn, Mark m, int i) {
        Player p0 = new ClientAIPlayer("1", Mark.XX, new NaiveStrategy());
        Player p1 = new ClientAIPlayer("2", Mark.OO, new NaiveStrategy());

        Game game = m == Mark.XX ? new Game(p0, p1, bn) : new Game(p1, p0, bn);
        if (m == game.startMonte()) {
            wins[i]++;
        } else {
            loss[i]++;
        }
    }
}
