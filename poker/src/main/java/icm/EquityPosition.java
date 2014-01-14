/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package icm;

/**
 *
 * @author mick
 */
public class EquityPosition {
    private double chipStack;
    public double equity;

    public EquityPosition(double chipStack) {
        this.chipStack = chipStack;
        this.equity = 0;
    }

    public double getChipStack() {
        return chipStack;
    }

    public void setChipStack(double chipStack) {
        this.chipStack = chipStack;
    }

    public double getEquity() {
        return equity;
    }

    public void setEquity(double equity) {
        this.equity = equity;
    }
}
