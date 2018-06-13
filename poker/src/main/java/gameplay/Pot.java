/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameplay;

import java.util.BitSet;

/**
 *
 * @author mick
 */
public class Pot {
    private double potSize;
    private boolean closed;
    private BitSet allInPlayersBitSet;
    private BitSet remPlayerBitSet;

    public Pot(double potSize, boolean closed) {
        this.potSize = potSize;
        this.closed = closed;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }
}