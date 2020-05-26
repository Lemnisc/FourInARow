package ai;

/**
 * Created by Bouke Willem on 16-12-2014.
 */

import gamelogic.Board;
import gamelogic.Mark;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NaiveStrategy implements Strategy {
    private Random random = new Random();

    @Override
    public String getName() {
        return "Naive";
    }

    @Override
    public int determineMove(Board b, Mark m) {
        Set<Integer> emptyfields = new HashSet<Integer>();
        for (Integer i = 0; i < b.HOR; i++) {
            if (b.colHasSpace(i)) {
                emptyfields.add(i);
            }
        }
        int amount = emptyfields.size();
        Integer[] efArray = new Integer[amount];
        efArray = emptyfields.toArray(efArray);

        int rNum = random.nextInt(amount);

        return efArray[rNum];
    }
}
