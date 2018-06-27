package util;

import spears2p2.Pair;

public class HeadsUpRangeEquites {
	private double[] heroRngWins = new double[169];
	private double[] heroRngLoss = new double[169];
	private double[] heroRngChop = new double[169];
	public double[] heroRngEqs = new double[169];
	
	public double  heroTotWins = 0;
	public double heroTotLoss = 0;
	public double heroTotChop = 0;
	public double heroTotEqs = 0;
	
	
	public HeadsUpRangeEquites(double[] heroRange, double[] villRange) {
		for (int hfIdx = 0; hfIdx < heroRange.length; hfIdx++) {
			for (int vfIdx = 0; vfIdx < villRange.length; vfIdx++) {
				heroRngWins[hfIdx] += HeadsUpPreFlopEquities.wins[hfIdx][vfIdx] * heroRange[hfIdx] * villRange[vfIdx] * Pair.preToPostOrdinals[hfIdx].length;
				heroRngLoss[hfIdx] += HeadsUpPreFlopEquities.loss[hfIdx][vfIdx] * heroRange[hfIdx] * villRange[vfIdx] * Pair.preToPostOrdinals[hfIdx].length;
				heroRngChop[hfIdx] += HeadsUpPreFlopEquities.chop[hfIdx][vfIdx] * heroRange[hfIdx] * villRange[vfIdx] * Pair.preToPostOrdinals[hfIdx].length;
			}
			
			heroRngEqs[hfIdx] = Utilities.calcEquity(heroRngWins[hfIdx], heroRngLoss[hfIdx], heroRngChop[hfIdx]);  
		}
		
		heroTotWins = Utilities.getDoubleArrayTotal(heroRngWins);
		heroTotLoss = Utilities.getDoubleArrayTotal(heroRngLoss);
		heroTotChop = Utilities.getDoubleArrayTotal(heroRngChop);
		heroTotEqs = Utilities.calcEquity(heroTotWins, heroTotLoss, heroTotChop);
	}
}
