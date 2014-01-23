package nash;

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
	
	public static int calcCallOrFold(double betSize, double[][] callStrats, double[] callFreqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		return calcCallOrFold(betSize, 0, callStrats, callFreqs, nutsRank, pairRankSets);
	}
	
	public static int calcCallOrFold(double betSize, double extraValue, double[][] callStrats, double[] callFreqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		int callThresh = 0;
		double callPct = (double)(1 - extraValue) / (double)(betSize + 1);
		double totCallFreqs = 0;
		double rankTotCallFreqs = 0;
		double partialFreq = 0;
		
		totCallFreqs = getDoubleArrayTotal(callFreqs);
		
		double remCallFreq = callPct * totCallFreqs;
		
		logger.debug("remCallFreq = "+remCallFreq+",totCallFreqs = "+totCallFreqs+", callPct = "+callPct+", extraValue = "+extraValue);
		
		for (HandRank key : pairRankSets.keySet()) {
			rankTotCallFreqs = getTotalRankFreq(callFreqs, pairRankSets.get(key));
			
			if (rankTotCallFreqs > 0) {			
				if (key.getRank() == nutsRank) {
					callThresh = nutsRank;
					for (PairRank pairRank: pairRankSets.get(key)) {
						if (pairRank.getRank() == nutsRank) {
							callStrats[pairRank.getOrdinal()][0] = 0;
							callStrats[pairRank.getOrdinal()][1] = 1;
							remCallFreq -= callFreqs[pairRank.getOrdinal()];
						}
					}
				} else {
					if (remCallFreq > rankTotCallFreqs) {
						callThresh = key.getRank();
						partialFreq = 1;
					} else if (remCallFreq <= 0) {
						partialFreq = 0;
					} else {
						callThresh = key.getRank();
						partialFreq = remCallFreq/rankTotCallFreqs;
						remCallFreq = -1;
					}
					
					for (PairRank pairRank: pairRankSets.get(key)) {
						if (callFreqs[pairRank.getOrdinal()] > 0) {
							callStrats[pairRank.getOrdinal()][0] = 1 - partialFreq;
							callStrats[pairRank.getOrdinal()][1] = partialFreq;
							remCallFreq -= (partialFreq * callFreqs[pairRank.getOrdinal()]);
						}
					}
				}
			}
		}
		
		return callThresh;
	}

	public static double[] calculateBluffRange(double betSize, double[][] checkOrBetStrats, double[] checkOrBetFreqs, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
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
