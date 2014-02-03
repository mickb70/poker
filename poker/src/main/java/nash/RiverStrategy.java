package nash;

import gametree.GameTree;
import gametree.TreeInvalidException;

import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import spears2p2.Pair;

public abstract class RiverStrategy {
	private static Logger logger = Logger.getLogger(RiverStrategy.class);
	
	private static double getDoubleArrayTotal(double[] arr) {
		double total = 0;
		
		for (int i = 0; i < arr.length; i++) {
			total += arr[i];
		}
		
		return total;		
	}

	private static double getTotalRankFreq(double[] callFreqs, TreeSet<PairRank> treeSet) {
		double ret = 0;
		
		for (PairRank pr : treeSet) {
			ret += callFreqs[pr.getOrdinal()];
		}
		
		return ret;
	}
	
	public static void calcChkOrBet(GameTree tree, double goalExp, int maxLoops) throws TreeInvalidException {
		GameTree noBluff = tree.deepCopy();
		double floor = 1;
		double ceilg = 0;
		
		for (int i = 0; i < maxLoops; i++) {
			double topPct = (floor + ceilg)/2;
			//guess calling strategy
			double[][] callStrats = new double[1326][2];
			double[] callFreqs = tree.getAdjFreqs()[1];
			int nutsRank = tree.getRoot().getBoardNode().getNutsRank();
			TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = tree.getRoot().getBoardNode().getPairRankSets();
			RiverStrategy.calcTopOfRange(topPct, 1, 0, callStrats, callFreqs, nutsRank, pairRankSets);
			
			tree.getRoot().getKids()[1].setStrats(callStrats);
			noBluff.getRoot().getKids()[1].setStrats(callStrats);
	
			noBluff.getRoot().removeBluffValue(noBluff.getRoot().getKids()[0]);		
			noBluff.setBestResponse(0);
			
			double[][] newBetStrats = noBluff.getRoot().getStrats();
			RiverStrategy.calcBluffRange(1, newBetStrats, noBluff.getAdjFreqs()[0], noBluff.getRoot().getBoardNode().getPairRankSets());
			tree.getRoot().setStrats(newBetStrats);
			
			PairValue[] bestRespPairValues = new PairValue[2];
			double exploit = tree.getStratExploitability(bestRespPairValues);
			
			if (exploit <= goalExp) {
				return;
			} else {
				//bluff
				if (true) {
					
				}
			}
		}
	}

	public static int calcCallOrFold(double betSize, double[][] callStrats, double[] callFreqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		return calcCallOrFold(betSize, 0, callStrats, callFreqs, nutsRank, pairRankSets);
	}
	
	public static int calcCallOrFold(double betSize, double extraValue, double[][] callStrats, double[] callFreqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		int callThresh = 0;
		double callPct = (double)(1 - extraValue) / (double)(betSize + 1);		
		int topIdx = 1;
		int botIdx = 0;
		
		callThresh = calcTopOfRange(callPct, topIdx, botIdx, callStrats, callFreqs, nutsRank, pairRankSets);
		
		return callThresh;
	}
	
	public static int calcTopOfRange(double topPct, int topIdx, int botIdx, double[][] strats, double[] freqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		int thresh = 0;
		double totTopFreqs = 0;
		double rankTotTopFreqs = 0;
		double partialFreq = 0;
		
		totTopFreqs = getDoubleArrayTotal(freqs);
		
		double remTopFreq = topPct * totTopFreqs;
		
		for (HandRank key : pairRankSets.keySet()) {
			rankTotTopFreqs = getTotalRankFreq(freqs, pairRankSets.get(key));
			
			if (rankTotTopFreqs > 0) {			
				if (key.getRank() == nutsRank) {
					thresh = nutsRank;
					for (PairRank pairRank: pairRankSets.get(key)) {
						if (pairRank.getRank() == nutsRank) {
							strats[pairRank.getOrdinal()][botIdx] = 0;
							strats[pairRank.getOrdinal()][topIdx] = 1;
							remTopFreq -= freqs[pairRank.getOrdinal()];
						}
					}
				} else {
					if (remTopFreq > rankTotTopFreqs) {
						thresh = key.getRank();
						partialFreq = 1;
					} else if (remTopFreq <= 0) {
						partialFreq = 0;
					} else {
						thresh = key.getRank();
						partialFreq = remTopFreq/rankTotTopFreqs;
						remTopFreq = -1;
					}
					
					for (PairRank pairRank: pairRankSets.get(key)) {
						if (freqs[pairRank.getOrdinal()] > 0) {
							strats[pairRank.getOrdinal()][botIdx] = 1 - partialFreq;
							strats[pairRank.getOrdinal()][topIdx] = partialFreq;
							remTopFreq -= (partialFreq * freqs[pairRank.getOrdinal()]);
						}
					}
				}
			}
		}
		
		return thresh;
	}

	public static double[] calcBluffRange(double betSize, double[][] checkOrBetStrats, double[] checkOrBetFreqs, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		double bluffPct = 1 / (betSize + 1);
		double totValueBetFreq = 0;
		double rankTotBluffFreqs = 0;
		double partialFreq = 0;
		double[] bluffFeqs = new double[1326];
		
		for (int i = 0; i < 1326; i++) {
			totValueBetFreq += (checkOrBetStrats[i][1] * checkOrBetFreqs[i]);
		}
		
		double remBluffFreq = bluffPct * totValueBetFreq;
		
		for (HandRank key : pairRankSets.descendingKeySet()) {
			rankTotBluffFreqs = getTotalRankFreq(checkOrBetFreqs, pairRankSets.get(key));
			
			if (rankTotBluffFreqs > 0) {
				if (remBluffFreq > rankTotBluffFreqs) {
					partialFreq = 1;
				} else if (remBluffFreq <= 0) {
					partialFreq = 0;
				} else {
					partialFreq = remBluffFreq/rankTotBluffFreqs;
					remBluffFreq = -1;
				}
				
				if (partialFreq > 0) {
					for (PairRank pairRank: pairRankSets.get(key)) {
						checkOrBetStrats[pairRank.getOrdinal()][0] = 1 - partialFreq;
						checkOrBetStrats[pairRank.getOrdinal()][1] = partialFreq;
						remBluffFreq -=  (partialFreq * checkOrBetFreqs[pairRank.getOrdinal()]);
						bluffFeqs[pairRank.getOrdinal()] = (partialFreq * checkOrBetFreqs[pairRank.getOrdinal()]);
					}
				}
			}
		}
		
		return bluffFeqs;
	}
}
