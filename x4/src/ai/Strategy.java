package ai;

import gamelogic.Board;
import gamelogic.Mark;

/**
 * Created by Bouke Willem on 16-12-2014.
 */
public interface Strategy {
    public String getName();

    public int determineMove(Board b, Mark m);
}
