package mvc;

import gamelogic.Board;

import java.util.Observer;

/**
 * Created by Bouke Willem on 22-1-2015.
 */
public interface ViewInterface extends Observer {
    public void start();

    public void showBoard(Board b);
}
