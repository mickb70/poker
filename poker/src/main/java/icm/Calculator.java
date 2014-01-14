/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icm;

import java.util.LinkedList;

/**
 *
 * @author mick
 */
public class Calculator {

    double payouts[];

    public Calculator(double[] payouts) {
        this.payouts = payouts;
    }

    public LinkedList<EquityPosition> calculate(double[] stacks) {
        LinkedList<EquityPosition> equities = new LinkedList<EquityPosition>();
        double chipsInPlay = 0;

        for (double stack : stacks) {
            equities.add(new EquityPosition(stack));
        }

        for (EquityPosition eq : equities) {
            chipsInPlay += eq.getChipStack();
        }

        calculatePath(equities, 0, 1, chipsInPlay);

        return equities;
    }
    
    public double[] calculateEqs(double[] stacks) {
    	double[] eqs;
    	int i = 0;
    	
    	LinkedList<EquityPosition> equities = calculate(stacks);
    	eqs = new double[equities.size()];
    	
    	for (EquityPosition eq : equities) {
            eqs[i++] = eq.getEquity();
        }
    	
    	return eqs;
    }

    private void calculatePath(LinkedList<EquityPosition> equities, int position, double nodeProbabilty, double chipsInPlay) {
        EquityPosition equity = null;
        double childNodeProbabality = 0;

        if (position >= payouts.length) {
            return;
        }
        
        for (int i = 0; i < equities.size(); i++) {
            equity = equities.removeFirst();
            childNodeProbabality = nodeProbabilty * (equity.getChipStack() / chipsInPlay);
            equity.equity +=  childNodeProbabality * payouts[position];
            calculatePath(equities, position + 1, childNodeProbabality, chipsInPlay - equity.getChipStack());
            equities.addLast(equity);
        }
    }
}

