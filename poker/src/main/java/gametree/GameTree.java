package gametree;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import nash.HandRank;
import nash.PairRank;
import nash.PairValue;
import nash.RiverStrategy;

import org.apache.log4j.Logger;

import spears2p2.Pair;

public class GameTree {
	private static Logger logger = Logger.getLogger(GameTree.class);
	
	private GameNode root;
	private double[][] freqs;
	// Adjusted Frequencies to allow for opponent range
	private double[][] adjFreqs;
	
	public GameTree() {
		
	}

	public int getNumberNodes() {
		return root.getNumberNodes();
	}
	
	public int getMaximumDepth() {
		return root.getNumberNodes();
	}
	
	public GameTree deepCopy() {
		GameTree copy = new GameTree();
		
		copy.root = root.deepCopy(null);
		
		copy.freqs = new double[freqs.length][];
		copy.adjFreqs = new double[adjFreqs.length][];
		
		for (int i = 0; i < freqs.length; i++) {
			copy.freqs[i] = Arrays.copyOf(freqs[i], freqs[i].length);
		}
		
		for (int i = 0; i < adjFreqs.length; i++) {
			copy.adjFreqs[i] = Arrays.copyOf(adjFreqs[i], adjFreqs[i].length);
		}
		
		return copy;
	}

	public static GameTree getRiverCheckOrBetSubTree(BoardNode boardNode, double[] checkShowDownPayoffs, double checkShowDownPot, double[] foldPayoffs, double[] betShowDownPayoffs, double betShowDownPot) {
		GameTree tree = new GameTree();
		
		GameNode nodeCheckOrBetChoice = new GameNode(null, 0, null, boardNode,GameNodeType.Action, null, 0, 2);
		GameNode nodeCheckPayoff = new GameNode(nodeCheckOrBetChoice, -1, null, boardNode, GameNodeType.Showdown, checkShowDownPayoffs, checkShowDownPot, 0);		
		GameNode nodeCallOrFoldChoice = new GameNode(nodeCheckOrBetChoice, 1, null, boardNode,GameNodeType.Action, null, 0, 2);
		GameNode nodeBetFoldPayoff = new GameNode(nodeCallOrFoldChoice,-1, null, boardNode,GameNodeType.Payoff, foldPayoffs, 0, 0);
		GameNode nodeBetCallPayoff = new GameNode(nodeCallOrFoldChoice,-1, null, boardNode,GameNodeType.Showdown, betShowDownPayoffs, betShowDownPot, 0);
		
		nodeCheckOrBetChoice.setGameTreeNode(0, nodeCheckPayoff);
		nodeCheckOrBetChoice.setGameTreeNode(1, nodeCallOrFoldChoice);
		nodeCallOrFoldChoice.setGameTreeNode(0, nodeBetFoldPayoff);
		nodeCallOrFoldChoice.setGameTreeNode(1, nodeBetCallPayoff);
		
		tree.root = nodeCheckOrBetChoice;
		
		return tree;
	}

	public GameNode getRoot() {
		return this.root;
	}
	
	public void initialiseAllStrats() {		
		this.root.initialiseAllStrats();		
	}

	public double[] getGameValues() {
		double[] gameValues = new double[2];
		double[] valueNumer = new double[2];
		double[] valueDenom = new double[2];
		double[][] stratValues = getPairValues();
		
		for (int i = 0; i < stratValues.length ; i++) {
			for (int j = 0; j < stratValues[i].length; j++) {
				valueNumer[i] += adjFreqs[i][j] * stratValues[i][j];
				valueDenom[i] += adjFreqs[i][j];
			}
		}
		
		for (int i = 0; i < gameValues.length; i++) {
			gameValues[i] = valueNumer[i]/valueDenom[i];
		}
		
		return gameValues;
	}
	
	public double[][] getPairValues() {
		double[][] stratValues = new double[2][1326];
		double[][] valueNumer = new double[2][1326];
		double[][] valueDenom = new double[2][1326];
		double[] tmpStratValues = new double[freqs.length];
		Pair[] pairs = new Pair[2];
		boolean computeRegret = false;
		
		for (Pair pair0: Pair.values()) {
			for (Pair pair1: Pair.values()) {
				if (!pair0.intersects(pair1)) {
					if ((freqs[0][pair0.ordinal]>0)&&(freqs[1][pair1.ordinal]>0)) {
						pairs[0] = pair0;
						pairs[1] = pair1;
						//TODO how do I allow for requencies
//						double multiplier = freqs[0][pair0.ordinal] * freqs[1][pair1.ordinal];
						tmpStratValues = this.root.getPairValues(1, pairs, computeRegret);
						valueNumer[0][pair0.ordinal] += freqs[1][pair1.ordinal] * tmpStratValues[0];
						valueDenom[0][pair0.ordinal] += freqs[1][pair1.ordinal];
						valueNumer[1][pair1.ordinal] += freqs[0][pair0.ordinal] * tmpStratValues[1];
						valueDenom[1][pair1.ordinal] += freqs[0][pair0.ordinal];
					}
				}
			}
		}
		
		for (int i = 0; i < stratValues.length ; i++) {
			for (int j = 0; j < stratValues[i].length; j++) {
				if (valueDenom[i][j] != 0) {
					stratValues[i][j] = valueNumer[i][j] / valueDenom[i][j];
				} else {
					stratValues[i][j] = 0;
				}
			}
		}
		
		return stratValues;
	}

	public void setFreqs(double[][] freqs) {
		this.freqs = new double[2][1326];
		
		for (int i = 0; i < freqs.length; i++) {
			for (int j = 0; j < freqs[i].length; j++) {
				if (!root.getBoardNode().getBoard().intersects(Pair.values()[j])) {
					this.freqs[i][j] = freqs[i][j];
				}
			}
		}
		
		this.adjFreqs = new double[2][1326];
		
		for (Pair pair0 : Pair.values()) {
			if (!root.getBoardNode().getBoard().intersects(pair0)) {
				for (Pair pair1 : Pair.values()) {
					if (!root.getBoardNode().getBoard().intersects(pair1)) {
						if (!pair0.intersects(pair1)) {
							adjFreqs[0][pair0.ordinal] += (freqs[0][pair0.ordinal]*freqs[1][pair1.ordinal]);
							adjFreqs[1][pair1.ordinal] += (freqs[0][pair0.ordinal]*freqs[1][pair1.ordinal]);
						}
					}
				}
			}
		}
			
	}
	
	public void validateTree() throws TreeInvalidException {
		root.validateTree(freqs);
	}

	public void setBestResponse(int heroIdx) throws TreeInvalidException {
		int maxDepth = root.getMaxDepth(0);
		Pair[] pairs = new Pair[2];
		boolean computeRegret = true;
		
		root.validateTree(freqs);
		
		for (int k = maxDepth; k >= 0; k--) {
			root.resetStratsAndRegrets(k,heroIdx);
			for (Pair pair0: Pair.values()) {
//				logger.debug("pair0 = "+pair0);
				for (Pair pair1: Pair.values()) {
					if (!pair0.intersects(pair1)) {
						if ((freqs[0][pair0.ordinal]>0)&&(freqs[1][pair1.ordinal]>0)) {
							pairs[0] = pair0;
							pairs[1] = pair1;
							double multiplier = freqs[0][pair0.ordinal] * freqs[1][pair1.ordinal];
//							logger.debug("pair0 = "+pair0+"["+freqs[0][pair0.ordinal]+"], pair1 = "+pair1+"["+freqs[1][pair1.ordinal]+"], multiplier = "+multiplier);
							root.getPairValues(multiplier, pairs, computeRegret);
						}
					}
				}
			}			
			root.calcExploitStrat(k,heroIdx);
		}
		
		return;
	}

	public double[][] getFreqs() {
		return freqs;
	}

	public double[][] getAdjFreqs() {
		return adjFreqs;
	}

	public double[][][] getStrats(int actIdx) {
		int numStrats = root.getNumStrats(actIdx);
		double[][][] strats = new double[numStrats][1326][];
		
		root.copyStrats(strats, actIdx, 0);
		
		return strats;
	}

	public void writeStrats(double[][][] strats, int actIdx) {
		root.writeStrats(strats, actIdx, 0);		
	}

	public double getStratExploitability() throws TreeInvalidException {
		double[] currGameValues = getGameValues();
		double[][] currPairValues = getPairValues();
		
		logger.debug("Current Game Values "+Arrays.toString(currGameValues));
		
//		TreeSet<PairValue> heroRangeSet = new TreeSet<PairValue>();
//		
//		for (int i = 0 ; i < currPairValues[0].length; i++) {
//			heroRangeSet.add(new PairValue(Pair.values()[i], currPairValues[0][i]));
//		}
//		
//		for (PairValue pairValue: heroRangeSet) {
//			if (adjFreqs[0][pairValue.getOrdinal()] > 0) {
//				logger.debug("hero -> "+pairValue.getPair()+" - "+pairValue.getValue()+" - "+Arrays.toString(root.getStrats()[pairValue.getOrdinal()]));
//			}
//		}
//		
//		TreeSet<PairValue> villRangeSet = new TreeSet<PairValue>();
//		
//		for (int i = 0 ; i < currPairValues[1].length; i++) {
//			villRangeSet.add(new PairValue(Pair.values()[i], currPairValues[1][i]));
//		}
//		
//		for (PairValue pairValue: villRangeSet) {
//			if (adjFreqs[1][pairValue.getOrdinal()] > 0) {
//				logger.debug("vill -> "+pairValue.getPair()+" - "+pairValue.getValue()+" - "+Arrays.toString(root.getKids()[1].getStrats()[pairValue.getOrdinal()]));
//			}
//		}
		
		double[][][] heroStrats = getStrats(0);
		double[][][] villStrats = getStrats(1);
		
		setBestResponse(0);
		double[] heroBestRespGameValues = getGameValues();
		double[][] heroBestRespPairValues = getPairValues();
		double[][][] heroExpStrats = getStrats(0);
		
		writeStrats(heroStrats, 0);
		setBestResponse(1);
		double[] villBestRespGameValues = getGameValues();
		double[][] villBestRespPairValues = getPairValues();
		double[][][] villExpStrats = getStrats(1);
		
		double heroExploitability = villBestRespGameValues[1] - currGameValues[1];
		double villExploitability = heroBestRespGameValues[0] - currGameValues[0];
		
		if ((villExploitability < 0) || (heroExploitability < 0)) {
			throw new TreeInvalidException("villExploitability = "+villExploitability+", heroExploitability = "+heroExploitability );
		}
		
		logger.debug("villExploitability = "+villExploitability+", heroExploitability = "+heroExploitability);
		
		TreeSet<PairValue> heroExpSet = new TreeSet<PairValue>();
		
		for (int i = 0 ; i < heroBestRespPairValues[0].length; i++) {
			heroExpSet.add(new PairValue(Pair.values()[i], (heroBestRespPairValues[0][i] - currPairValues[0][i])));
		}
		
		int lowFruit = 0;
		for (PairValue pairValue: heroExpSet) {
			if (adjFreqs[0][pairValue.getOrdinal()] > 0) {
				if (lowFruit++ <10) {
					logger.debug("hero -> "+pairValue.getPair()+" - "+pairValue.getValue()+" - "+heroBestRespPairValues[0][pairValue.getOrdinal()]+" > "+currPairValues[0][pairValue.getOrdinal()]+
					Arrays.toString(heroExpStrats[0][pairValue.getOrdinal()])+" > "+Arrays.toString(heroStrats[0][pairValue.getOrdinal()]));
				}
			}
		}
		
		TreeSet<PairValue> villExpSet = new TreeSet<PairValue>();
		
		for (int i = 0 ; i < villBestRespPairValues[1].length; i++) {
			villExpSet.add(new PairValue(Pair.values()[i], (villBestRespPairValues[1][i] - currPairValues[1][i])));
		}
		
		lowFruit = 0;
		for (PairValue pairValue: villExpSet) {
			if (adjFreqs[1][pairValue.getOrdinal()] > 0) {
				if (lowFruit++ <10) {
					logger.debug("vill -> "+pairValue.getPair()+" - "+pairValue.getValue()+" - "+villBestRespPairValues[1][pairValue.getOrdinal()]+" > "+currPairValues[1][pairValue.getOrdinal()]+
					Arrays.toString(villExpStrats[0][pairValue.getOrdinal()])+" > "+Arrays.toString(villStrats[0][pairValue.getOrdinal()]));
				}
			}
		}
		
		return villExploitability + heroExploitability;
	}

	public GameTree findNashEqLastAct() throws TreeInvalidException {
		GameTree copy = this.deepCopy();
		GameTree noBluff = this.deepCopy();
		
		double[][] callStrats = new double[1326][2];
		double[] callFreqs = copy.getAdjFreqs()[1];
		int nutsRank = copy.getRoot().getBoardNode().getNutsRank();
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = copy.getRoot().getBoardNode().getPairRankSets();
		RiverStrategy.calcCallOrFold(1, callStrats, callFreqs, nutsRank, pairRankSets);
		
		noBluff.getRoot().getKids()[1].setStrats(callStrats);		
		noBluff.getRoot().removeBluffValue();
		
		noBluff.setBestResponse(0);
		
		double[][] newBetStrats = noBluff.getRoot().getStrats();
		RiverStrategy.calculateBluffRange(1, newBetStrats, noBluff.getAdjFreqs()[0], noBluff.getRoot().getBoardNode().getPairRankSets());
		
		copy.getRoot().setStrats(newBetStrats);
		copy.getRoot().getKids()[1].setStrats(callStrats);
		
		return copy;
	}
	
	public GameTree findNashEqLastAct(int numLoops) throws TreeInvalidException {
		GameTree looper = this.deepCopy();
		GameTree noBluff = this.deepCopy();
		GameTree ret = this.deepCopy();
		
//		looper.getRoot().initialiseStratsAndRegrets();
//		noBluff.getRoot().initialiseStratsAndRegrets();
		
		for (int i = 0; i < numLoops; i++) {
			double[][] newFreqs = new double[2][1326];
			double[][] callStrats = new double[1326][2];
			
			//remove check back from hero range
			newFreqs[1] = Arrays.copyOf(looper.getFreqs()[1], looper.getFreqs()[1].length);
			for (int j = 0; j < 1326; j++) {
				newFreqs[0][i] = looper.getRoot().getStrats()[j][1];
			}
			
			looper.setFreqs(newFreqs);
			
			double[] callFreqs = looper.getAdjFreqs()[1];
			int nutsRank = looper.getRoot().getBoardNode().getNutsRank();
			TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = looper.getRoot().getBoardNode().getPairRankSets();
			RiverStrategy.calcCallOrFold(1, callStrats, callFreqs, nutsRank, pairRankSets);
			
			noBluff.getRoot().getKids()[1].setStrats(callStrats);	
			noBluff.getRoot().removeBluffValue();
			
			noBluff.setBestResponse(0);
			
			double[][] newBetStrats = noBluff.getRoot().getStrats();
			RiverStrategy.calculateBluffRange(1, newBetStrats, noBluff.getAdjFreqs()[0], noBluff.getRoot().getBoardNode().getPairRankSets());
			
			looper.getRoot().setStrats(newBetStrats);
			looper.getRoot().getKids()[1].setStrats(callStrats);
			
			ret.getRoot().setStrats(newBetStrats);
			ret.getRoot().getKids()[1].setStrats(callStrats);
		}
		
		return ret;
	}
}