package gamelogic;

import mvc.ViewTUI;

/**
 * Created by Bouke Willem on 28-1-2015.
 */
public class LocalGameController {


    private static Board board = new Board();
    private static ViewTUI view = new ViewTUI(board);

    public LocalGameController(Player player1, Player player2) {
        board.addObserver(view);
        Game game = new Game(player1, player2, board);
        game.start();
    }
}

