/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameplay;

import spears2p2.Pair;

/**
 *
 * @author mick
 */
public class Player {
    private String name;
    private double chipStack;
    private double chipsBehind;
    private double chipsInFront;
    private boolean allIn;
    private Pair pair;

    public Player() {
    }
    
    public Player(String name, double chipStack) {
        this.name = name;
        this.chipStack = chipStack;
    }

    public double getChipStack() {
        return chipStack;
    }

    public void setChipStack(double chipStack) {
        this.chipStack = chipStack;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public double getChipsBehind() {
        return chipsBehind;
    }

    public void setChipsBehind(double chipsBehind) {
        this.chipsBehind = chipsBehind;
    }

    public double getChipsInFront() {
        return chipsInFront;
    }

    public void setChipsInFront(double chipsInFront) {
        this.chipsInFront = chipsInFront;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pair getPair() {
		return pair;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public double anteUp(double ante) {
        if (chipStack > ante) {
            chipStack -= ante;
            chipsBehind -= ante;
            chipsInFront += ante;
            return ante;
        } else {
            chipsInFront += chipStack;
            chipStack = 0;
            chipsBehind = 0;
            allIn = true;
            return chipsInFront;
        }
    }

    public double pushChipsIn() {
        double chips = chipsInFront;
        chipsInFront = 0;
        return chips;
    }
}
