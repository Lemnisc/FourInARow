package gamelogic;

/**
 * Represents a mark in the Four game. There three possible values:
 * Mark.XX, Mark.OO and Mark.EM.
 * Module 2 lab assignment
 *
 * @author Theo Ruys
 * @version $Revision: 1.4 $
 */
public enum Mark {

    EM, XX, OO;

    /**
     * Returns the other mark.
     *
     * @return the other mark is this mark is not EM or EM
     */
    /*@
       ensures this == Mark.XX ==> \result == Mark.OO;
       ensures this == Mark.OO ==> \result == Mark.XX;
       ensures this == Mark.EM ==> \result == Mark.EM;
     */
    public Mark other() {
        if (this == XX) {
            return OO;
        } else if (this == OO) {
            return XX;
        } else {
            return EM;
        }
    }
}
