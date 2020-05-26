// TODO Check ties: Call isFull() somewhere.
// To make a tie: [0, 6, 1, 5, 6, 0, 5, 1, 0, 6, 1, 5, 6, 0, 5, 1, 0, 6, 1, 5, 6, 0, 5, 1, 4, 2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 3]
package gamelogic;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;


/**
 * Created by Bouke Willem on 22-1-2015.
 */
public class Board extends Observable {
    // -- Constants --------------------------------------------------

    public static final int HOR = 7;
    public static final int VER = 6;
    public static final int SZE = HOR * VER;

    public int[] diagLeft = {27, 34, 38, 39, 40, 41}; //Ending points of \ diagonals
    public int[] diagRight = {21, 28, 35, 36, 37, 38}; //Ending points of / diagonals
    public List<Integer> movesSoFar = new LinkedList<>();

    public static final String NUMBERING = "  0 |  1 |  2 |  3 |  4 |  5 |  6";

    // -- Instance variables -----------------------------------------
    /**
     * The HOR by VER fields of the Four-in-a-row board. See NUMBERING for the
     * coding of the fields.
     */
    /*@
       private invariant fields.length == SZE;
       invariant (\forall int i; 0 <= i & i < SZE;
           getField(i) == Mark.EM || getField(i) == Mark.XX || getField(i) == Mark.OO);
     */
    private Mark[] fields;
    public Mark hasWinner;


    // -- Constructors -----------------------------------------------


    /**
     * Creates an empty board.
     */
    /*@
       ensures (\forall int i; 0 <= i & i < SZE; this.getField(i) == Mark.EM);
     */
    public Board(/*FourController fc*/) {
        this.fields = new Mark[SZE];
        for (int i = 0; i < SZE; i++) {
            this.setField(i, Mark.EM);
        }
    }


    // -- Queries ----------------------------------------------------

    //@pure
    public Mark[] getBoard() {
        return fields;
    }


    /**
     * Creates a deep copy of this board.
     *
     * @return a copy of the board
     */
    /*@
       ensures \result != this;
       ensures (\forall int i; 0 <= i & i < HOR * VER; \result.getField(i) == this.getField(i));
     */
    public Board deepCopy() {
        Board copy = new Board();
        for (int i = 0; i < SZE; i++) {
            copy.setField(i, getField(i));
        }
        return copy;
    }

    /**
     * Calculates the index in the linear array of fields from a (row, col)
     * pair.
     *
     * @param row the row of the field
     * @param col the col of the field
     * @return the index belonging to the (row,col)-field
     */
    /*@
       requires 0 <= row & row < VER;
       requires 0 <= col & col < HOR;
     */
    public int index(int row, int col) {
        return col + (row * HOR);
    }

    /**
     * Returns column belonging to index to play in
     *
     * @param i the field for which the column needs to be known
     * @return the column belonging to the index
     */
    /*@
        requires 0<=i & i<HOR;
     */
    public int[] colOfIn(int i) {
        int[] markList = new int[6];
        for (int j = 0; j < VER; j++) {
            markList[j] = i + j * HOR;
        }
        return markList;
    }

    /**
     * Returns row belonging to index to play in
     *
     * @param i the field for which the row needs to be known
     * @return the row belonging to the index
     */
    /*@
        requires 0<=i & i<VER;
     */
    public int[] rowOfIn(int i) {
        int[] markList = new int[7];
        int start = HOR * (i / HOR);
        for (int j = 0; j < HOR; j++) {
            markList[j] = start + j;
        }
        return markList;
    }


    /*@
        requires 0<=col & col<HOR;
        ensures \result == isEmptyField(col);
     */
    /*@ pure */
    public boolean colHasSpace(int col) {
        for (int j : colOfIn(col)) {
            if (isEmptyField(j)) {
                return true;
            }
        }
        return false;
    }

    /*@
        requires 0<=col & col<HOR;
        ensures getField(field) == mark;
     */
    public void setInCol(int col, Mark mark) {
        int[] column = colOfIn(col);
        int field = 0;
        if (colHasSpace(col)) {

            for (int i = 0; i < VER; i++) {
                if (!isEmptyField(column[i])) {
                    setField(column[i - 1], mark);
                    field = column[i - 1];
                    break;
                }
                if (i == VER - 1) {
                    setField(column[i], mark);
                    field = column[i];
                    break;
                }
            }

            setChanged();
            notifyObservers("Move made");

            if (isWinner(field, mark)) {
                hasWinner = mark;
                //System.out.println("Winning move: " + col);
                setChanged();
                notifyObservers(mark);
            }
        } else {
            System.out.println("Column has no space to place mark.");
            setChanged();
            notifyObservers("No space");
        }
    }

    /**
     * Returns true if <code>ix</code> is a valid index of a field on the board.
     *
     * @param ix the index of the field to check
     * @return <code>true</code> if <code>0 &lt;= ix &lt; DIM*DIM</code>
     */
    /*@
       ensures \result == (0 <= ix && ix < SZE);
     */
    /*@pure*/
    public boolean isField(int ix) {
        return (0 <= ix && ix < SZE);
    }

    /**
     * Returns true if <code>ix</code> is a valid index of a column on the board.
     *
     * @param ix the index of the column to check
     * @return <code>true</code> if <code>0 &lt;= ix &lt; HOR</code>
     */
    /*@
       ensures \result == (0 <= ix && ix < HOR);
     */
    /*@pure*/
    public boolean isCol(int ix) {
        return (0 <= ix && ix < HOR);
    }


    /**
     * Returns the content of the field <code>i</code>.
     *
     * @param i the number of the field
     * @return the mark on the field
     */
    /*@
       requires this.isField(i);
       ensures \result == Mark.EM || \result == Mark.XX || \result == Mark.OO;
     */
    public Mark getField(int i) {
        return fields[i];
    }


    /**
     * Returns the content of the field referred to by the (row,col) pair.
     *
     * @param row the row of the field
     * @param col the column of the field
     * @return the mark on the field
     */
    /*@
       requires this.isField(row,col);
       ensures \result == Mark.EM || \result == Mark.XX || \result == Mark.OO;
     */
    public Mark getField(int row, int col) {
        return fields[index(row, col)];
    }


    /**
     * Returns true if the field <code>i</code> is empty.
     *
     * @param i the index of the field (see NUMBERING)
     * @return true if the field is empty
     */
    /*@
       requires this.isField(i);
       ensures \result == (this.getField(i) == Mark.EM);
     */
    public boolean isEmptyField(int i) {
        return this.isField(i) && fields[i] == Mark.EM;
    }


    /**
     * Tests if the whole board is full.
     *
     * @return true if all fields are occupied
     */
    /*@
       ensures \result == (\forall int i; i <= 0 & i < SZE; this.getField(i) != Mark.EM);
     */
    /*@pure*/
    public boolean isFull() {
        for (Mark i : this.fields) {
            if (i == Mark.EM) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns true if the game is over. The game is over when there is a winner
     * or the whole student is full.
     *
     * @return true if the game is over
     */
    /*@
       ensures \result == this.isFull() || this.hasWinner();
     */
    /*@pure*/
    public boolean gameOver() {
        if (this.isFull()) {
            setChanged();
        }
        return (this.isFull() || this.hasWinner != null);
    }

    /**
     * Checks whether there is a horizontal four and only contains the mark
     * <code>m</code>.
     *
     * @param m   the mark of interest
     * @param loc the location for which to check if it completes a four
     * @return true if there is a horizontal four controlled by <code>m</code>
     */
    /*@
        requires 0 <= loc & loc < SZE;
     */
    public boolean hasHorizontal(int loc, Mark m) {
        int count;
        count = 0;
        int[] theRow = rowOfIn(loc);
        for (int i = 0; i < HOR; i++) {
            if (getField(theRow[i]) == m) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    /**
     * Checks whether there is a vertical four and only contains the mark
     * <code>m</code>.
     *
     * @param m   the mark of interest
     * @param loc the location for which to check if it completes a four
     * @return true if there is a vertical four controlled by <code>m</code>
     */
    /*@
        requires 0 <= loc & loc < SZE;
     */
    public boolean hasVertical(int loc, Mark m) {
        if (loc <= 20) {
            int count;
            int i = loc;
            count = 1;
            while (i < SZE - HOR) {
                if (getField(i + HOR) == m) {
                    count++;
                    if (count == 4) {
                        return true;
                    }
                } else return false;
                i = i + HOR;
            }
        }
        return false;
    }

    /**
     * Checks whether there is a diagonal four and only contains the mark
     * <code>m</code>.
     *
     * @param m the mark of interest
     * @return true if there is a vertical four controlled by <code>m</code>
     */
    /*@
        requires m == Mark.XX || m == Mark.OO;
     */
    public boolean hasDiagonal(Mark m) {
        //For both diagonal options, go through the board,
        //staying within limits.
        //If you count four in a row, there are four in a row.
        for (int i : diagLeft) {
            int count = (getField(i) == m) ? 1 : 0;
            while (i - (HOR + 1) > 0 && i % HOR != 0) {
                if (getField(i - (HOR + 1)) == m) {
                    count++;
                    if (count == 4) {
                        return true;
                    }
                } else count = 0;
                i = i - (HOR + 1);
            }
        }
        for (int i : diagRight) {
            int count = (getField(i) == m) ? 1 : 0;
            while (i - (HOR - 1) > 0 && (i - (HOR - 1)) % HOR != 0) {
                if (getField(i - (HOR - 1)) == m) {
                    count++;
                    if (count == 4) {
                        return true;
                    }
                } else count = 0;
                i = i - (HOR - 1);
            }
        }
        return false;
    }

    /**
     * Checks if the mark <code>m</code> has won. A mark wins if it controls at
     * least one horizontal, vertical, or diagonal four.
     *
     * @param m   the mark of interest
     * @param loc the location for which to check if it completes a four
     * @return true if the mark has won
     */
    /*@
       requires m == Mark.XX | m == Mark.OO;
       ensures \result == this.hasRow(m) || this.hasColumn(m) || this.hasDiagonal(m);
     */
    /*@pure*/
    public boolean isWinner(int loc, Mark m) {
        if (this.hasHorizontal(loc, m)); //System.out.println("Horizontal won");
        if (this.hasVertical(loc, m)); //System.out.println("Vertical won");
        if (this.hasDiagonal(m)); //System.out.println("Diagonal won");
        return (this.hasHorizontal(loc, m) || this.hasVertical(loc, m) || this.hasDiagonal(m));
    }

    /**
     * Empties all fields of this student (i.e., let them refer to the value
     * Mark.EM).
     */
    /*@
       ensures (\forall int i; 0 <= i & i < HOR * VER; this.getField(i) == Mark.EM);
     */
    public void reset() {
        for (int i = 0; i < SZE; i++) {
            this.hasWinner = null;
            this.setField(i, Mark.EM);
            movesSoFar.clear();
        }
        setChanged();
        notifyObservers("Move made (reset)");
    }


    /**
     * Sets the content of field <code>i</code> to the mark <code>m</code>.
     *
     * @param i the field number (see NUMBERING)
     * @param m the mark to be placed
     */
    /*@
       requires this.isField(i);
       ensures this.getField(i) == m;
     */
    public void setField(int i, Mark m) {
        if (this.isField(i)) {
            this.fields[i] = m;
        }
    }
}
