/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameplay;

/**
 *
 * @author mick
 */
public class Action {
    public final int ANTE = 0;
    public final int SMALLBLIND = 1;
    public final int BIGBLIND = 2;
    public final int RAISE = 3;
    public final int SMALLRAISE = 4;

    private double costToStay;
    private double lastRaise;
    private int lastRaiserIdx;
    private int lastAction;

    public Action() {
    }

    public double getCostToStay() {
        return costToStay;
    }

    public void setCostToStay(double costToStay) {
        this.costToStay = costToStay;
    }

    public int getLastAction() {
        return lastAction;
    }

    public void setLastAction(int lastAction) {
        this.lastAction = lastAction;
    }

    public double getLastRaise() {
        return lastRaise;
    }

    public void setLastRaise(double lastRaise) {
        this.lastRaise = lastRaise;
    }

    public int getLastRaiserIdx() {
        return lastRaiserIdx;
    }

    public void setLastRaiserIdx(int lastRaiserIdx) {
        this.lastRaiserIdx = lastRaiserIdx;
    }
}