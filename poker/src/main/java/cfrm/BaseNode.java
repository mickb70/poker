package cfrm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class BaseNode {
	private static Logger logger = Logger.getLogger(BaseNode.class);
	
	// constants for actor indexes that are not players
	protected final static int PAYOUT = 100;
	protected final static int SHOWDN = 101;

	//members that relate this node to a tree
	//Base node of original tree for cloning itself if original
	private final TreeIdx treeIdx;
	protected BaseNode root;
	protected BaseNode dad;
	
	protected int actIdx;
	protected int depth;
	
	// strategy and regret x[numStratChoices][numChanceOpts]
	protected double nodeStrats[][]; 
	protected double nodeRegret[][];
	protected int nodeVisits[];
	
	//descendants of this node and cost to have them :)
	protected BaseNode kids[];
	protected double bets[];
	
	//this is what players have put in before arriving at this node.
	protected double pot;
	//the slice of pie at this node
	protected double payouts[];
	
	//not sure why this exists
	//public static final int idxSeed = 0;
	//this should be instantiated and incremented on root node
	
	//derived from incremented idxSeq on root node
	public int idx;
	
	//number of chance options at this node
	private final int numChanceOpts;
	private final String nameSpace;

	public BaseNode(int numChanceOpts, String nameSpace, TreeIdx treeIdx) {
		super();
		this.numChanceOpts = numChanceOpts;
		this.nameSpace = nameSpace;
		this.treeIdx = treeIdx;
	}	

	protected void initialiseStrats(boolean split) {
		if (null != kids) {
			nodeStrats = new double[kids.length][numChanceOpts];
			nodeRegret = new double[kids.length][numChanceOpts];
			nodeVisits = new int[numChanceOpts];
			//initialise strategies
			for (int i = 0; i < kids.length; i++) {
				if (split) {
					Arrays.fill(nodeStrats[i], (double)1/(double)kids.length);
				} else {
					Arrays.fill(nodeStrats[i], 0);
				}
			}
		}
	}
	
	private String createStratFileName(String name) {
		return "temp/" + nameSpace + "-" +name+ "-" +this.idx + ".ser";
	}
	
	public double compareStats(String prev, String next) throws FileNotFoundException, IOException, ClassNotFoundException {
		double diff = 0;
		if (kids != null) {
			String prevName = createStratFileName(prev);
			String nextName = createStratFileName(next);
			ObjectInputStream s = new ObjectInputStream(new FileInputStream(
					prevName));
			double prevStr[][] = (double[][])s.readObject();
			s.close();
			s = new ObjectInputStream(new FileInputStream(
					nextName));
			double nextStr[][] = (double[][])s.readObject();
			s.close();
			for (int i = 0; i < nextStr.length; i++) {
				for (int j = 0; j < nextStr[i].length; j++) {
					diff += Math.abs(nextStr[i][j]-prevStr[i][j]);
				}
			}
			for (int i = 0; i < kids.length; i++) {
				diff += kids[i].compareStats(prev,next);
			}
			
		}
		return diff;
	}
	
	public void writeStrats(String name) throws IOException {
		if (kids != null) {
			String fileName = createStratFileName(name);
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(
					fileName));
			s.writeObject(nodeStrats);
			for (int i = 0; i < kids.length; i++) {
				kids[i].writeStrats(name);
			}
			s.close();
		}		
	}
	
	public void calcExploitStrat(int expDepth, int expIdx) {
		if ((actIdx == expIdx)  && (depth == expDepth)) {
			logger.debug(actIdx+","+depth);
			for (int i = 0; i < nodeVisits.length; i++) {
				calcPureStrategy(i);
			}
		}
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				kids[i].calcExploitStrat(expDepth, expIdx);
			}
		}
	}

	public void resetStrats(int maxDepth, int cleanIdx) {
		if (kids != null) {
			if ((actIdx == cleanIdx)&&(depth<=maxDepth)) {
				initialiseStrats(true);
			}
			for (int i = 0; i < kids.length; i++) {
				kids[i].resetStrats(maxDepth,cleanIdx);
			}			
		}
	}
	
	public int getMaxDepth(int maxDepth, int depthIdx) {
		int newMaxDepth = 0;
		if (actIdx == depthIdx) {
			newMaxDepth = depth;
		}
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				newMaxDepth =  Math.max(newMaxDepth,kids[i].getMaxDepth(newMaxDepth, depthIdx));
			}
		}
		return newMaxDepth;
	}
	
	public void initStratAndRegret(double[][][] regret, double[][][] strat) {
		if (kids != null) {
			regret[idx] = new double[nodeRegret.length][];
			strat[idx] = new double[nodeRegret.length][];
			for (int i = 0; i < nodeRegret.length; i++) {
				regret[idx][i] = new double[nodeRegret[i].length];
				strat[idx][i] = new double[nodeStrats[i].length];
			}
			
			for (int i = 0; i < kids.length; i++) {
				kids[i].initStratAndRegret(regret, strat);
			}
		}
	}
	
	public void getStratAndRegret(double[][][] regret, double[][][] strat) {	
		if (kids != null) {
			for (int i = 0; i < nodeRegret.length; i++) {
				for (int j = 0; j < nodeRegret[i].length; j++) {
					try {
					regret[idx][i][j] = nodeRegret[i][j];
					strat[idx][i][j] = nodeStrats[i][j];
					} catch (Exception e) {
						System.err.println(idx+","+i+","+j);
						System.exit(42);
					}
				}
			}
			
			for (int i = 0; i < kids.length; i++) {
				kids[i].getStratAndRegret(regret, strat);
			}
		}
	}

	public void setStratAndRegret(double[][][] regret, double[][][] strat, int stratIdx) {
		System.out.println("fred");
		
		if (actIdx == stratIdx) {
			for (int i = 0; i < nodeRegret.length; i++) {
				nodeRegret[i] = Arrays.copyOf(regret[idx][i], nodeRegret[i].length);
				logger.info(idx+" - "+Arrays.toString(strat[idx][i]));
				nodeStrats[i] = Arrays.copyOf(strat[idx][i], nodeStrats[i].length);
			}
		}
		
		if (kids != null) {
			for (int i = 0; i < kids.length; i++) {
				kids[i].setStratAndRegret(regret, strat, stratIdx);
			}
		}
		
	}

	public void cleanNotVisited() {
		if (kids != null) {
			for (int k = 0; k < kids.length; k++) {
				kids[k].cleanNotVisited();
				for (int i = 0; i < nodeVisits.length; i++) {
					if (nodeVisits[i] == 0) {
						for (int j = 0; j < nodeStrats.length; j++) {
							nodeStrats[j][i] = -1;
						}
					}
				}
			}
		}
	}
	
	protected void recalculateStrategy(int chanceIdx) {
		double denom = 0;
		for (int i = 0; i < kids.length; i++) {
			if (nodeRegret[i][chanceIdx] > 0) {
				denom += nodeRegret[i][chanceIdx];
			}
		}
		if (denom > 0) {
			for (int i = 0; i < kids.length; i++) {
				if (nodeRegret[i][chanceIdx] > 0) {
					nodeStrats[i][chanceIdx] = nodeRegret[i][chanceIdx]/denom;
				} else {
					nodeStrats[i][chanceIdx] = 0;
				}
			}
		} else {
			for (int i = 0; i < kids.length; i++) {
				nodeStrats[i][chanceIdx] = (double)1/(double)kids.length;
			}
		}		
	}
	
	private void calcPureStrategy(int chanceIdx) {
		double max = nodeRegret[0][chanceIdx];
		boolean maxFound = false;
		for (int i = 0; i < kids.length; i++) {
			if (nodeRegret[i][chanceIdx] > 0) {
				max = Math.max(nodeRegret[i][chanceIdx],max);
			}
		}
//		logger.info("max = "+max+", "+Pair.pfvalues[chanceIdx].toString());
		for (int i = 0; i < kids.length; i++) {
			if (!maxFound &&(nodeRegret[i][chanceIdx] == max)) {
				nodeStrats[i][chanceIdx] = 1;
				maxFound = true;
			} else {
				nodeStrats[i][chanceIdx] = 0;
			}
		}		
	}

	protected String printStrategy(int chanceIdx) {
		StringBuffer str = new StringBuffer();
		
		for (int i = 0; i < bets.length; i++) {
			str.append("bet ["+bets[i]+"] weight = ["+nodeStrats[i][chanceIdx]+"],");
		}
		
		return str.toString();
	}

	protected void calculateRegret(double parentStrat, int chanceIdx, double currStrat,
			double[] atomStrat) {
		if (parentStrat <= 0) {
			return;
		}
		
		for (int i = 0; i < kids.length; i++) {
			logger.debug(">>> River Regret = "+nodeRegret[i][chanceIdx]);
			logger.debug("parentStrat="+parentStrat+",atomStrat[i]="+atomStrat[i]+",currStrat="+currStrat+
					"into formula riverRegret[i][chanceIdx] += parentStrat * (atomStrat[i] - currStrat)");
			nodeRegret[i][chanceIdx] += parentStrat * (atomStrat[i] - currStrat);
			logger.debug("<<< River Regret = "+nodeRegret[i][chanceIdx]);
		}
	}
	
	public double[] wash(double multiplier, double parentStrat, int gameStateIdx, int[] chanceIndexes, EquityFunction eqf, boolean computeRegret, boolean computeStrat) {
		//TODO fix this heads up only
		double expRet[] = new double[2];
		double currStrat = 0;
		double atomStrat[];
		
		if (actIdx == PAYOUT) {
			expRet[0] += payouts[0];
			expRet[1] += payouts[1];
		} else if (actIdx == SHOWDN) {
			expRet[0] += (double)((double)pot * eqf.getEquity(gameStateIdx, chanceIndexes[0], chanceIndexes[1]));
			expRet[1] += (double)((double)pot * eqf.getEquity(gameStateIdx, chanceIndexes[1], chanceIndexes[0]));
		} else {
			//TODO fix this heads up only
			double tmpExpRet[][] = new double[2][];
			int actChanceIdx = chanceIndexes[actIdx];
			atomStrat = new double[kids.length];
			double ev = 0;
			for (int i = 0; i < kids.length; i++) {
				try {
//					double fred[] = kids[i].wash(multiplier, nodeStrats[i][actChanceIdx], gameStateIdx, chanceIndexes, eqf, computeRegret, computeStrat);
//					System.out.println(fred);
					tmpExpRet[i] = kids[i].wash(multiplier, nodeStrats[i][actChanceIdx], gameStateIdx, chanceIndexes, eqf, computeRegret, computeStrat);
				} catch (Exception e) {
					System.out.println("oops");
				}
				ev = tmpExpRet[i][actIdx]-bets[i];
				logger.debug(">>>currStrat="+currStrat+",tempExpRet["+actIdx+"]="+tmpExpRet[actIdx]+",nodeStrats="+nodeStrats[i][actChanceIdx]);
				currStrat += (ev * nodeStrats[i][actChanceIdx]);
				logger.debug("<<<currStrat="+currStrat+",tempExpRet["+actIdx+"]="+tmpExpRet[actIdx]+",nodeStrats="+nodeStrats[i][actChanceIdx]);
				atomStrat[i] = ev;
			}
			if (computeRegret) {
				nodeVisits[actChanceIdx]++;
				logger.debug("parentStrat="+parentStrat+",currStrat="+currStrat+",atomStrat="+Arrays.toString(atomStrat));
				calculateRegret(multiplier*parentStrat,actChanceIdx,currStrat,atomStrat);
				logger.debug(">>>actIdx="+actIdx+",Strategy - "+printStrategy(actChanceIdx));
				if (computeStrat) {
					recalculateStrategy(actChanceIdx);
				}
				logger.debug("<<<actIdx="+actIdx+",Strategy - "+printStrategy(actChanceIdx));
			}
			//TODO push recalced strat up tree
			for (int i = 0; i < kids.length; i++) {
				for (int j = 0; j < tmpExpRet[i].length; j++) {
					expRet[j] += tmpExpRet[i][j] * nodeStrats[i][actChanceIdx];
				}
			}			
		}
		return expRet;
	}
	
	public BaseNode deepCopy(BaseNode dad) {
		BaseNode copy = new BaseNode(this.numChanceOpts, this.nameSpace, this.treeIdx);
		
		if (null == dad) {
			copy.root = copy;
		} else {
			copy.root = dad.root;
		}
		
		copy.actIdx = this.actIdx;
		
		if (null != this.bets) {
			copy.bets = Arrays.copyOf(this.bets, this.bets.length);
		}
		
		copy.dad = dad;
		copy.depth = this.depth;
		copy.idx = this.idx;
		
		if (null != this.kids) {
			copy.kids = new BaseNode[this.kids.length];
			
			for (int i = 0; i < this.kids.length; i++) {
				copy.kids[i] = kids[i].deepCopy(copy);
			}

			copy.initialiseStrats(true);
		}
		
		if (null != this.payouts) {
			copy.payouts = Arrays.copyOf(this.payouts, this.payouts.length);
		}
		
		copy.pot = this.pot;
		
		return copy;
	}

	public int getNodeCount() {
		return this.treeIdx.idxSeq;
	}

	
	public double[] getStratValues(double[] heroStratVals, double[] villStratVals, int[] heroFreqs, int[] villFreqs, MonteCarloMachine mcm,
			EquityFunction ef) {
		// TODO Auto-generated method stub
//		double[] getStratVals(HandState hs, int[] heroRange, int[][] villRange, double[] heroIndVal, double[] villIndVal) {
		double[] stratVals = new double[2];
		double[] expRets = new double[2];
//		int count = 0;
//		int heroCount = 0;
//		int indVillCount[] = new int[169];
		mcm.reset();
		int chanceIndexes[];
		
		while (mcm.hasNext()) {
//			hs.pairs[0] = Pair.pfvalues[i];
//			heroCount = 0;
			chanceIndexes = mcm.getNextMatch();
			//TODO 1 and 1 works for flat array only and no gameStateID
			expRets = root.wash(1, 1, 0, chanceIndexes, ef, false, false);
			stratVals[0] += expRets[0];
			stratVals[1] += expRets[1];
		}
		stratVals[0]/=(double)mcm.getFlatArraySize();
		stratVals[1]/=(double)mcm.getFlatArraySize();
		return stratVals;
	}

	public void setExploitStrat(int i) {
		// TODO Auto-generated method stub
		
	}
}
