package gametree;

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
						double multiplier = freqs[0][pair0.ordinal] * freqs[1][pair1.ordinal];
						tmpStratValues = this.root.getPairValues(multiplier,pairs, computeRegret);
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
		
		//TODO set only possible freqs???
//		for (Pair pair0: Pair.values()) {
//			for (Pair pair1: Pair.values()) {
//				if (!pair0.intersects(pair1)) {
//					if ((freqs[0][pair0.ordinal]>0)&&(freqs[1][pair1.ordinal]>0)) {
//						this.freqs[0][pair0.ordinal] = freqs[0][pair0.ordinal];
//						this.freqs[1][pair1.ordinal] = freqs[1][pair1.ordinal];
//					}
//				}
//			}
//		}
		
		for (int i = 0; i < freqs.length; i++) {
			for (int j = 0; j < freqs[i].length; j++) {
				this.freqs[i][j] = freqs[i][j];
			}
		}
		
		this.adjFreqs = new double[2][1326];
		
		for (Pair pair0 : Pair.values()) {
			for (Pair pair1 : Pair.values()) {
				if (!pair0.intersects(pair1)) {
					adjFreqs[0][pair0.ordinal] += (freqs[0][pair0.ordinal]*freqs[1][pair1.ordinal]);
					adjFreqs[1][pair1.ordinal] += (freqs[0][pair0.ordinal]*freqs[1][pair1.ordinal]);
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
				for (Pair pair1: Pair.values()) {
					if (!pair0.intersects(pair1)) {
						if ((freqs[0][pair0.ordinal]>0)&&(freqs[1][pair1.ordinal]>0)) {
							pairs[0] = pair0;
							pairs[1] = pair1;
							double multiplier = freqs[0][pair0.ordinal] * freqs[1][pair1.ordinal];
							logger.debug("pair0 = "+pair0+"["+freqs[0][pair0.ordinal]+"], pair1 = "+pair1+"["+freqs[1][pair1.ordinal]+"], multiplier = "+multiplier);
							root.getPairValues(multiplier, pairs, computeRegret);
						}
					}
				}
			}			
			root.calcExploitStrat(k,heroIdx);
		}
		
		return;
	}

	public double[][] getAdjFreqs() {
		return adjFreqs;
	}
}
