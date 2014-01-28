package gametree;

import java.io.FileWriter;
import java.io.IOException;
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
	/**
	 * Array of frequencies 
	 * [actIdx][pair.ordinal]
	 */
	private double[][] freqs;
	/**
	 * Array of frequencies adjusted for opponent range
	 * [actIdx][pair.ordinal]
	 */
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
		return deepCopy(root);
	}
	
	/**
	 * Make a copy of subtree (or tree where root is passed in as argument
	 * all objects are new instantiated objects
	 * 
	 * @param root the node in the tree to make root node
	 * @return copy of game tree or subtree
	 */
	public GameTree deepCopy(GameNode root) {
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
	
	public static GameTree getRiverCcOrCfOrBetSubTree(BoardNode boardNode, double[] payOffsChkChkSD, double potChkChkSD, double[] payOffsChkBetFoldPO, double[] payOffsChkBetCllSD, double potChkBetCllSD, double[] payOffsBetFoldPO, double[] payOffsBetCllSD, double potBetCllSD) {
		GameTree tree = new GameTree();		
		int heroIdx = 0;
		int villIdx = 1;
		int termIdx = -1;
		
		GameNode nodeChkOrBet = new GameNode(null, heroIdx, null, boardNode,GameNodeType.Action, null, 0, 2);		
		 GameNode nodeChkbkOrBet = new GameNode(nodeChkOrBet, villIdx, null, boardNode,GameNodeType.Action, null, 0, 2);
		  GameNode nodeChkChkSD = new GameNode(nodeChkbkOrBet, termIdx, null, boardNode, GameNodeType.Showdown, payOffsChkChkSD, potChkChkSD, 0);
		  GameNode nodeChkCllOrFld = new GameNode(nodeChkOrBet, heroIdx, null, boardNode,GameNodeType.Action, null, 0, 2);
		   GameNode nodeChkBetFoldPO = new GameNode(nodeChkbkOrBet, termIdx, null, boardNode, GameNodeType.Showdown, payOffsChkBetFoldPO, 0, 0);
		   GameNode nodeChkBetCllSD = new GameNode(nodeChkbkOrBet, termIdx, null, boardNode, GameNodeType.Showdown, payOffsChkBetCllSD, potChkBetCllSD, 0);
		 GameNode nodeCllOrFld = new GameNode(nodeChkOrBet, villIdx, null, boardNode,GameNodeType.Action, null, 0, 2);
		  GameNode nodeBetFoldPO = new GameNode(nodeChkbkOrBet, termIdx, null, boardNode, GameNodeType.Showdown, payOffsBetFoldPO, 0, 0);
		  GameNode nodeBetCllSD = new GameNode(nodeChkbkOrBet, termIdx, null, boardNode, GameNodeType.Showdown, payOffsBetCllSD, potBetCllSD, 0);

		
		nodeChkOrBet.setGameTreeNode(0, nodeChkbkOrBet);
		nodeChkOrBet.setGameTreeNode(1, nodeCllOrFld);
		
		nodeChkbkOrBet.setGameTreeNode(0, nodeChkChkSD);
		nodeChkbkOrBet.setGameTreeNode(1, nodeChkCllOrFld);
		
		nodeCllOrFld.setGameTreeNode(0, nodeBetFoldPO);
		nodeCllOrFld.setGameTreeNode(1, nodeBetCllSD);
		
		nodeChkCllOrFld.setGameTreeNode(0, nodeChkBetFoldPO);
		nodeChkCllOrFld.setGameTreeNode(1, nodeChkBetCllSD);
		
		tree.root = nodeChkOrBet;
		
		return tree;
	}

	public static GameTree getRiverChkOrBetSubTree(BoardNode boardNode, double[] ChkShowDownPayoffs, double ChkShowDownPot, double[] foldPayoffs, double[] betShowDownPayoffs, double betShowDownPot) {
		GameTree tree = new GameTree();
		
		GameNode nodeChkOrBetChoice = new GameNode(null, 0, null, boardNode,GameNodeType.Action, null, 0, 2);
		GameNode nodeChkPayoff = new GameNode(nodeChkOrBetChoice, -1, null, boardNode, GameNodeType.Showdown, ChkShowDownPayoffs, ChkShowDownPot, 0);		
		GameNode nodeCllOrFoldChoice = new GameNode(nodeChkOrBetChoice, 1, null, boardNode,GameNodeType.Action, null, 0, 2);
		GameNode nodeBetFoldPayoff = new GameNode(nodeCllOrFoldChoice,-1, null, boardNode,GameNodeType.Payoff, foldPayoffs, 0, 0);
		GameNode nodeBetCllPayoff = new GameNode(nodeCllOrFoldChoice,-1, null, boardNode,GameNodeType.Showdown, betShowDownPayoffs, betShowDownPot, 0);
		
		nodeChkOrBetChoice.setGameTreeNode(0, nodeChkPayoff);
		nodeChkOrBetChoice.setGameTreeNode(1, nodeCllOrFoldChoice);
		nodeCllOrFoldChoice.setGameTreeNode(0, nodeBetFoldPayoff);
		nodeCllOrFoldChoice.setGameTreeNode(1, nodeBetCllPayoff);
		
		tree.root = nodeChkOrBetChoice;
		
		return tree;
	}

	public GameNode getRoot() {
		return this.root;
	}
	
	public void setRoot(GameNode root) {
		this.root = root;
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
		
//		logger.debug(Arrays.toString(freqs[0]));
//		logger.debug(Arrays.toString(freqs[1]));
		
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

	/**
	 * @return Array of frequencies [actIdx][pair.ordinal]
	 */
	public double[][] getFreqs() {
		return freqs;
	}

	/**
	 * @return Array of adjusted frequencies [actIdx][pair.ordinal]
	 */
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
		PairValue[] bestRespPairValues = new PairValue[2];
		
		return getStratExploitability(bestRespPairValues);
	}

	public double getStratExploitability(PairValue[] bestRespPairValues) throws TreeInvalidException {
		GameTree copy = this.deepCopy();
		double[] currGameValues = copy.getGameValues();
		double[][] currPairValues = copy.getPairValues();
		
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
		
		int heroIdx = copy.getRoot().getActIdx();
		int villIdx = copy.getRoot().getKids()[1].getActIdx();
		double[][][] heroStrats = copy.getStrats(heroIdx);
		double[][][] villStrats = copy.getStrats(villIdx);
		
		copy.setBestResponse(heroIdx);
		double[] heroBestRespGameValues = copy.getGameValues();
		double[][] heroBestRespPairValues = copy.getPairValues();
		double[][][] heroBestRespStrats = copy.getStrats(heroIdx);
		
		copy.writeStrats(heroStrats, heroIdx);
		copy.setBestResponse(villIdx);
		double[] villBestRespGameValues = copy.getGameValues();
		double[][] villBestRespPairValues = copy.getPairValues();
		double[][][] villBestRespStrats = copy.getStrats(villIdx);
		
		double heroExploitability = villBestRespGameValues[villIdx] - currGameValues[villIdx];
		double villExploitability = heroBestRespGameValues[heroIdx] - currGameValues[heroIdx];
		
		if ((villExploitability < 0) || (heroExploitability < 0)) {
			throw new TreeInvalidException("villExploitability = "+villExploitability+", heroExploitability = "+heroExploitability );
		}
		
		logger.debug("villExploitability = "+villExploitability+", heroExploitability = "+heroExploitability);
		
		TreeSet<PairValue> heroBestRespSet = new TreeSet<PairValue>();
		
		for (int i = 0 ; i < heroBestRespPairValues[heroIdx].length; i++) {
			heroBestRespSet.add(new PairValue(Pair.values()[i], (heroBestRespPairValues[heroIdx][i] - currPairValues[heroIdx][i]), heroBestRespPairValues[heroIdx][i], currPairValues[heroIdx][i], (heroBestRespPairValues[heroIdx][i] - currPairValues[heroIdx][i])));
		}
		
		int lowFruit = 0;
		for (PairValue pairValue: heroBestRespSet) {
			if (adjFreqs[heroIdx][pairValue.getOrdinal()] > 0) {
				if (lowFruit++ <10) {
					logger.debug("hero -> "+pairValue.getPair()+" - "+pairValue.getValue()+
							" - "+heroBestRespPairValues[heroIdx][pairValue.getOrdinal()]+" > "+currPairValues[heroIdx][pairValue.getOrdinal()]);
					for (int i = 0; i < heroBestRespStrats.length; i++) {
						logger.debug(">>>"+i+ " - "+Arrays.toString(heroBestRespStrats[i][pairValue.getOrdinal()])+" > "+Arrays.toString(heroStrats[i][pairValue.getOrdinal()]));
					}
				}
			}
		}
		
		logger.debug("Pair = "+heroBestRespSet.first().getPair()+", value = "+heroBestRespSet.first().getValue() );
		if (heroBestRespSet.first().getValue() > 0) {
			bestRespPairValues[heroIdx] = heroBestRespSet.first().copy();
		} else {
			bestRespPairValues[heroIdx] = null;
		}
		
		TreeSet<PairValue> villExpSet = new TreeSet<PairValue>();
		
		for (int i = 0 ; i < villBestRespPairValues[1].length; i++) {
			villExpSet.add(new PairValue(Pair.values()[i], (villBestRespPairValues[villIdx][i] - currPairValues[villIdx][i]), villBestRespPairValues[villIdx][i], currPairValues[villIdx][i], (villBestRespPairValues[villIdx][i] - currPairValues[villIdx][i])));
		}
		
		lowFruit = 0;
		for (PairValue pairValue: villExpSet) {
			if (adjFreqs[villIdx][pairValue.getOrdinal()] > 0) {
				if (lowFruit++ <10) {
					logger.debug("vill -> "+pairValue.getPair()+" - "+pairValue.getValue()+
							" - "+villBestRespPairValues[villIdx][pairValue.getOrdinal()]+" > "+currPairValues[villIdx][pairValue.getOrdinal()]);
					for (int i = 0; i < villBestRespStrats.length; i++) {
						logger.debug(">>>"+i+ " - "+Arrays.toString(villBestRespStrats[i][pairValue.getOrdinal()])+" > "+Arrays.toString(villStrats[i][pairValue.getOrdinal()]));
					}
				}
			}
		}
		
		if (villExpSet.first().getValue() > 0) {
			bestRespPairValues[villIdx] = villExpSet.first().copy();
		} else {
			bestRespPairValues[villIdx] = null;
		}
		
		logger.debug(Arrays.toString(bestRespPairValues));
		
		return villExploitability + heroExploitability;
	}

//	public GameTree findNashEqLastAct() throws TreeInvalidException {
//		GameTree copy = this.deepCopy();
//		GameTree noBluff = this.deepCopy();
//		
//		double[][] CllStrats = new double[1326][2];
//		double[] CllFreqs = copy.getAdjFreqs()[1];
//		int nutsRank = copy.getRoot().getBoardNode().getNutsRank();
//		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = copy.getRoot().getBoardNode().getPairRankSets();
//		RiverStrategy.calcCllOrFold(1, CllStrats, CllFreqs, nutsRank, pairRankSets);
//		
//		noBluff.getRoot().getKids()[1].setStrats(CllStrats);		
//		noBluff.getRoot().removeBluffValue();
//		
//		noBluff.setBestResponse(0);
//		
//		double[][] newBetStrats = noBluff.getRoot().getStrats();
//		RiverStrategy.calculateBluffRange(1, newBetStrats, noBluff.getAdjFreqs()[0], noBluff.getRoot().getBoardNode().getPairRankSets());
//		
//		copy.getRoot().setStrats(newBetStrats);
//		copy.getRoot().getKids()[1].setStrats(CllStrats);
//		
//		return copy;
//	}
	
	public GameTree findNashEqChkOrBetSubTree(double betSize, double goalExp, int maxLoops) throws TreeInvalidException, NotSolvedException {
		GameTree looper = this.deepCopy();
		GameTree noBluff = this.deepCopy();
		GameTree ret = this.deepCopy();
		PairValue[] bestRespPairValues = new PairValue[2];
		double exploitability = 0;
		int heroIdx = this.root.getActIdx();
		int villIdx = this.root.getKids()[1].getActIdx();
		
		double[] bluffFreqs = new double[1326];
		bluffFreqs = Arrays.copyOf(looper.getFreqs()[heroIdx], looper.getFreqs()[heroIdx].length);
		
//		looper.getRoot().initialiseStratsAndRegrets();
//		noBluff.getRoot().initialiseStratsAndRegrets();
		
		for (int i = 0; i < maxLoops; i++) {
			double[][] newFreqs = new double[2][1326];
			double[][] CllStrats = new double[1326][2];
			
			//remove Chk back from hero range
			newFreqs[heroIdx] = Arrays.copyOf(bluffFreqs, bluffFreqs.length);
			newFreqs[villIdx] = Arrays.copyOf(looper.getFreqs()[villIdx], looper.getFreqs()[villIdx].length);
			
			looper.setFreqs(newFreqs);
			
			double[] CllFreqs = looper.getAdjFreqs()[villIdx];
			int nutsRank = looper.getRoot().getBoardNode().getNutsRank();
			TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = looper.getRoot().getBoardNode().getPairRankSets();
			
			if (bestRespPairValues[heroIdx] != null) {
				logger.debug("Pair = "+bestRespPairValues[heroIdx].getPair()+", value = "+bestRespPairValues[heroIdx].getValue() );
				double extraValue = bestRespPairValues[heroIdx].getOldValue() - 1;
				RiverStrategy.calcCallOrFold(betSize, extraValue, CllStrats, CllFreqs, nutsRank, pairRankSets);
			} else {
				logger.debug("Pair is null");
				RiverStrategy.calcCallOrFold(betSize, CllStrats, CllFreqs, nutsRank, pairRankSets);
			}
					
			noBluff.getRoot().getKids()[1].setStrats(CllStrats);	
			noBluff.getRoot().removeBluffValue(noBluff.getRoot().getKids()[0]);
			
			noBluff.setBestResponse(noBluff.getRoot().getActIdx());
			
			double[][] newBetStrats = noBluff.getRoot().getStrats();
			bluffFreqs = RiverStrategy.calculateBluffRange(betSize, newBetStrats, noBluff.getAdjFreqs()[heroIdx], noBluff.getRoot().getBoardNode().getPairRankSets());
			
			looper.getRoot().setStrats(newBetStrats);
			looper.getRoot().getKids()[1].setStrats(CllStrats);
			
			ret.getRoot().setStrats(newBetStrats);
			ret.getRoot().getKids()[1].setStrats(CllStrats);
			
			exploitability = ret.getStratExploitability(bestRespPairValues);
			
			logger.debug("exploitability = "+exploitability+", goalExp = "+goalExp);
			if (exploitability < goalExp) {
				return ret;
			}
		}
		
		throw new NotSolvedException("unable to solve");
	}

	public GameTree findNashEqRiverCcOrCfOrBetSubTree(double betSize, double goalExp, int maxLoops) throws TreeInvalidException, NotSolvedException {
		GameTree ret = this.deepCopy();
		GameTree copy = this.deepCopy();
		int heroIdx = root.getActIdx();
		int villIdx = 1 - root.getActIdx();
		
		double[] CllFreqs = copy.getAdjFreqs()[villIdx];
		int nutsRank = copy.getRoot().getBoardNode().getNutsRank();
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = copy.getRoot().getBoardNode().getPairRankSets();
		double[][] CllStrats = new double[1326][2];
		
		RiverStrategy.calcCallOrFold(betSize, CllStrats, CllFreqs, nutsRank, pairRankSets);
		
		copy.getRoot().getKids()[1].setStrats(CllStrats);		
		copy.getRoot().removeBluffValue(copy.getRoot().getKids()[0].getKids()[0]);
		copy.setBestResponse(heroIdx);
		
//		logger.debug(copy);
		
		double[][] newBetStrats = copy.getRoot().getStrats();		
		RiverStrategy.calculateBluffRange(betSize, newBetStrats, copy.getAdjFreqs()[heroIdx], copy.getRoot().getBoardNode().getPairRankSets());
		
		ret.getRoot().setStrats(newBetStrats);
		ret.getRoot().getKids()[1].setStrats(CllStrats);

		double[][] endFreqs = new double[2][1326];
		GameTree end = new GameTree();
		endFreqs[villIdx] = ret.getAdjFreqs()[villIdx];		
		for (int i = 0; i < 1326; i++) {
			if (ret.getAdjFreqs()[heroIdx][i] > 0) {
				endFreqs[heroIdx][i] = (newBetStrats[i][0]==0)?0.000001:newBetStrats[i][0];
			}
		}		
		end.setRoot(ret.getRoot().getKids()[0]);
		end.setFreqs(endFreqs);
		
		GameTree copyEnd = end.findNashEqChkOrBetSubTree(1, goalExp, maxLoops);
		
		ret.getRoot().getKids()[0].setStrats(copyEnd.getRoot().getStrats());
		ret.getRoot().getKids()[0].getKids()[1].setStrats(copyEnd.getRoot().getKids()[1].getStrats());

		return ret;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("\n"+root.printTree());
		
		for (int i = 0; i < freqs.length; i++){
			buf.append("\nactIdx = "+i+"\n");
			for (int j = 0; j < 1326; j++) {
				if (freqs[i][j] > 0) {
					buf.append("Pair = "+Pair.values()[j]+", adjFreq = "+adjFreqs[i][j]+", freq = "+freqs[i][j]+"\n");
					buf.append(root.printStrategy(i,j));
				}
			}
		}
		
		return buf.toString();
	}
	
	public void writeXml(String fileName) throws IOException {
		FileWriter fw = new FileWriter(fileName);
		
		fw.write("<tree>");
		
		
		
		fw.write("</tree>");
		
		fw.close();
	}
}