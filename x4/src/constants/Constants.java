package constants;

/**
 * Created by Over on 27.01.2015.
 */
public class Constants {
    public final static String JOIN_COMMAND = "join";   // join-command = "join", space, player-name , space , group-number, [extension-list] ;
    // e.g. "join etienne 27"
    public final static String READY_COMMAND = "ready_for_game";
    public final static String ERROR_COMMAND = "error";
    public final static String DOMOVE_COMMAND = "do_move"; // do_move col // e.g. do_move 6
    public final static String DONE_MOVE_COMMAND = "done_move";
    public final static String ACCEPT_COMMAND = "accept";
    public final static String REQUEST_MOVE_COMMAND = "request_move"; //"request_move player"
    public final static String START_GAME_COMMAND = "start_game"; // "start_game p1 p2"
    public final static String GAME_END_COMMAND = "game_end"; // If winner: includes name of winner. If tie: no parameters.

    public final static String OWN_GROUP_NUMBER = "27";

    public final static String SERVERUSAGE = "Expected parameter: <port>";

    public final static String ERROR1 = "001";
    public final static String ERROR2 = "002";
    public final static String ERROR3 = "003";
    public final static String ERROR4 = "004";
    public final static String ERROR5 = "005";
    public final static String ERROR6 = "006";
    public final static String ERROR7 = "007";
    public final static String ERROR8 = "008";
    public final static String ERROR9 = "009";
    public final static String ERROR10 = "010";
    public final static String ERROR11 = "011";
}
