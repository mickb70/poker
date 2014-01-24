package gametree;

import java.util.Arrays;

import org.apache.log4j.Logger;

import spears2p2.Card;
import spears2p2.Pair;
import spears2p2.Rank;
import spears2p2.Suit;

public class GameNode {
	private static Logger logger = Logger.getLogger(GameNode.class);
	
	private final GameNode dad;
	private final int actIdx;
	private final ActionNode actionNode;
	private final BoardNode boardNode;
	private final GameNodeType nodeType;
	private final double[] payOffs;
	private final double pot;
	private final int depth;
	
	private GameNode[] kids;
	
	private double[][] strats; 
	private double[][] regrets;
	private int[] visits;
	
	public GameNode(GameNode dad, int actIdx, ActionNode actionNode, BoardNode boardNode, GameNodeType nodeType, 
			double[] payOffs, double pot, int numKids) {		
		super();
		this.dad = dad;
		this.actIdx = actIdx;
		this.actionNode = actionNode;
		this.boardNode = boardNode;
		this.nodeType = nodeType;
		
		if (dad == null) {
			this.depth = 0;
		} else {
			this.depth = dad.getDepth() + 1;
		}
		
		if (payOffs != null) {
			this.payOffs = Arrays.copyOf(payOffs, payOffs.length);
		} else {
			this.payOffs = null;
		}
		this.pot = pot;
		
		if (numKids == 0) {
			this.kids = null;
			this.strats = null;
		} else {
			this.kids = new GameNode[numKids];
			this.strats = new double[1326][numKids];
			this.regrets = new double[1326][numKids];
			this.visits = new int[1326];
		}
	}
	
	public GameNode deepCopy(GameNode dad) {
		GameNode node = null;
		if (kids != null) {
			node = new GameNode(dad, this.actIdx, this.actionNode, this.boardNode, this.nodeType, 
					(this.payOffs==null)?null:Arrays.copyOf(this.payOffs, this.payOffs.length), this.pot, kids.length);
			node.strats = new double[1326][];
			for (int i = 0; i < kids.length; i++) {
				node.kids[i] = kids[i].deepCopy(node);
			}
			for (int i = 0; i < this.strats.length; i++) {
				node.strats[i] = Arrays.copyOf(this.strats[i], this.strats[i].length);
			}
		} else {
			node = new GameNode(dad, this.actIdx, this.actionNode, this.boardNode, this.nodeType, 
					(this.payOffs==null)?null:Arrays.copyOf(this.payOffs, this.payOffs.length), this.pot, 0);
		}
        return node;
    }
	
	public void initialiseStratsAndRegrets() {
		if (kids != null) {
			double fill = (double)1/(double)kids.length;
			visits = new int[1326];
			regrets = new double[1326][kids.length];
			for (int i = 0; i < 1326; i++) {
				Arrays.fill(strats[i], fill);
			}
		}
	}
	
	public void initialiseAllStrats() {
		if (kids != null) {
			for (GameNode kid: kids) {
				kid.initialiseAllStrats();
			}
			initialiseStratsAndRegrets();
		}
	}
	
	public void setGameTreeNode(int idx, GameNode gameNode) {
		this.kids[idx] = gameNode;
	}
	
	public int getNumberNodes() {
		int numKids = 0;
		
		if (kids != null) {
			for (GameNode kid: kids) {
				numKids += kid.getNumberNodes();
			}
		}
		
		return numKids + 1;
	}
	
	private void  calcPureStrategy(int ordinal) {
		double max = regrets[ordinal][0];
		//TODO debug only
		double[] localRegrets = regrets[ordinal];
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		if (aces.ordinal == ordinal) {
			System.currentTimeMillis();
		}
		
		boolean maxFound = false;
		for (int i = kids.length -1 ; i > -1; i--) {
			if (regrets[ordinal][i] > 0) {
				max = Math.max(regrets[ordinal][i],max);
			}
		}
		for (int i = kids.length -1 ; i > -1; i--) {
			if (!maxFound && (regrets[ordinal][i] == max)) {
				strats[ordinal][i] = 1;
				maxFound = true;
			} else {
				strats[ordinal][i] = 0;
			}
		}		
	}
	
	public void calcExploitStrat(int expDepth, int expIdx) {
		if ((actIdx == expIdx)  && (depth == expDepth)) {
			for (int i = 0; i < 1326; i++) {
				calcPureStrategy(i);
			}
		}
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				kids[i].calcExploitStrat(expDepth, expIdx);
			}
		}
	}
	
	protected void calculateRegret(double multiplier, Pair pair, double currStrat, double[] atomicStrats) {
		//TODO debug only
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		if (aces.ordinal == pair.ordinal) {
			System.currentTimeMillis();
		}
		
		for (int i = 0; i < kids.length; i++) {
			regrets[pair.ordinal][i] += multiplier * (atomicStrats[i] - currStrat);
		}
	}
	
	public double[] getPairValues(double multiplier, Pair[] pairs, boolean computeRegret) {
		double[] ret = new double[2];
		double[] tmpRet = new double[2];
		double currStrat = 0;
		
		if (kids != null) {
			double[] localStrats = strats[pairs[actIdx].ordinal];
			double[] atomicStrats = new double[kids.length];
			for (int i = 0; i < kids.length; i++) {
				double newMultiplier = this.strats[pairs[actIdx].ordinal][i];
				tmpRet = kids[i].getPairValues(newMultiplier, pairs, computeRegret);
				
				ret[0] += localStrats[i] * tmpRet[0];
				ret[1] += localStrats[i] * tmpRet[1];
				
				currStrat += localStrats[i] * ret[actIdx];
				atomicStrats[i] = tmpRet[actIdx];
			}
			if (computeRegret) {
				visits[pairs[actIdx].ordinal]++;
//				logger.debug("pair0 = "+pairs[0]+", pair1 = "+pairs[1]+", currentStrat = "+currStrat+", atomic = "+Arrays.toString(atomicStrats));
				calculateRegret(multiplier, pairs[actIdx], currStrat, atomicStrats);
			}
		} else {
			ret[0] += payOffs[0];
			ret[1] += payOffs[1];
			
			if (this.nodeType == GameNodeType.Showdown) {
				if (this.boardNode.getRank(pairs[0]) > this.boardNode.getRank(pairs[1])) {
					ret[0] += pot;
				} else if (this.boardNode.getRank(pairs[1]) > this.boardNode.getRank(pairs[0])) {
					ret[1] += pot;
				} else {
					ret[0] += pot/2;
					ret[1] += pot/2;
				}
			}
		}
		
		return ret;
	}
	
	public void resetStratsAndRegrets(int maxDepth, int resetIdx) {
		if (kids != null) {
			if ((actIdx == resetIdx)&&(depth<=maxDepth)) {
				initialiseStratsAndRegrets();
			}
			for (int i = 0; i < kids.length; i++) {
				kids[i].resetStratsAndRegrets(maxDepth,resetIdx);
			}			
		}
	}
	
	public int getMaxDepth(int prevMaxDepth) {
		int newMaxDepth = prevMaxDepth;
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				newMaxDepth =  kids[i].getMaxDepth(newMaxDepth);
			}
		}
		
		return Math.max(depth, newMaxDepth);
	}
	
	private void validateStrat(int ordinal) throws TreeInvalidException {
		double sum = 0;
		
		for (double d:strats[ordinal]) {
			sum += d;
		}
		
		if (Math.abs(sum - 1) >= .01) {
			throw new TreeInvalidException("Pair "+Pair.values()[ordinal]+", actid = "+actIdx+", strats "+Arrays.toString(strats[ordinal]));
		}
	}

	public void validateTree(double[][] freqs) throws TreeInvalidException {
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				kids[i].validateTree(freqs);

				for (int j = 0; j < freqs[actIdx].length; j ++) {
					double localFreq  = freqs[actIdx][j];
					if (localFreq > 0) {
						validateStrat(j);
					}
				}
			}
		}
	}
	
	public int getNumStrats(int actIdx) {
		int numStrats = 0;
		
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				numStrats += kids[i].getNumStrats(actIdx);
			}
			
			if (this.actIdx == actIdx) {
				numStrats++;
			}
		}
		
		return numStrats;
	}
	
	public int copyStrats(double[][][] strats, int actIdx, int idx) {
		if (kids != null) {
			if (this.actIdx == actIdx) {
				for (int i = 0; i < this.strats.length; i++) {
					strats[idx][i] = Arrays.copyOf(this.strats[i], this.strats[i].length);
				}
				idx++;
			}
			for (int i = 0; i < kids.length; i++) {
				idx = kids[i].copyStrats(strats, actIdx, idx);
			}
		}
		return idx;
	}
	
	public int writeStrats(double[][][] strats, int actIdx, int idx) {
		if (kids != null) {
			if (this.actIdx == actIdx) {
				for (int i = 0; i < this.strats.length; i++) {
					this.strats[i] = Arrays.copyOf(strats[idx][i], strats[idx][i].length);
				}
				idx++;
			}
			for (int i = 0; i < kids.length; i++) {
				idx = kids[i].writeStrats(strats, actIdx, idx);
			}
		}
		return idx;
	}
	
	public void removeBluffValue(GameNode checkBack) {
		kids[1].setGameTreeNode(0, checkBack);
	}

	public double[][] getStrats() {
		return strats;
	}

	public GameNode[] getKids() {
		return kids;
	}

	public BoardNode getBoardNode() {
		return boardNode;
	}

	public int getActIdx() {
		return actIdx;
	}

	public void setStrats(double[][] strats) {
		this.strats = strats;
	}

	public int getDepth() {
		return depth;
	}
}