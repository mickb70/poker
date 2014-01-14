package nash;

import java.util.TreeMap;
import java.util.TreeSet;

import spears2p2.Pair;

public abstract class RiverStrategy {
	
	private static double getDoubleArrayTotal(double[] arr) {
		double total = 0;
		
		for (double i : arr) {
			total += i;
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
	
	public static int calcCallOrFold(double betSize, double[] callStrats, double[] callFreqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		int callThresh = 0;
		double callPct = 1 / (betSize + 1);
		double totCallFreqs = getDoubleArrayTotal(callFreqs);
		double rankTotCallFreqs = 0;
		double partialFreq = 0;
		
		double remCallFreq = callPct * totCallFreqs;
		
		for (HandRank key : pairRankSets.keySet()) {
			rankTotCallFreqs = getTotalRankFreq(callFreqs, pairRankSets.get(key));
			
			if (key.getRank() == nutsRank) {
				callThresh = nutsRank;
				for (PairRank pairRank: pairRankSets.get(key)) {
					if (pairRank.getRank() == nutsRank) {
						callStrats[pairRank.getOrdinal()] = 1;
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
					callStrats[pairRank.getOrdinal()] = partialFreq;
					remCallFreq -= partialFreq;
				}
			}
		}
		
		return callThresh;
	}

	public static void calcBetOrCheckPair(double betSize,double[] betStrats, double[] betFreqs, double[] callStrats, double[] callFreqs, int nutsRank, TreeMap<HandRank, TreeSet<PairRank>> pairRankSets) {
		double bluffPct = 1 / (betSize + 1);
		double sumBetFreqs = 0;
		
		for (double freq : betFreqs) {
			sumBetFreqs += freq;
		}
		
		int callThresh = calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		
	}
}
