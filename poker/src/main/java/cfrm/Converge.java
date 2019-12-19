package cfrm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

public class Converge {

	private static Logger logger = Logger.getLogger(Converge.class);
	
	private static String createStratFileName(String name, int accuracy, double effectiveStacks) {
		return "temp/" + name + "/" + accuracy + "/"+ effectiveStacks + ".ser";
	}
		
	private static double[][][] checkFile(String name, int accuracy, double effectiveStacks) throws Exception {
		File f;
		String fileName = createStratFileName(name, accuracy, effectiveStacks);
		double strategy[][][] = null;
		
		f = new File(fileName);
		if (f.exists()) {
			logger.info("File ["+fileName+"]");
			ObjectInputStream s = new ObjectInputStream(new FileInputStream(fileName));
			strategy = (double[][][]) s.readObject();

			logger.info("i Length ["+strategy.length+"]");
			for (int i = 0; i < strategy.length; i++) {
				if (null != strategy[i]) {
					logger.info("j Length ["+strategy[i].length+"]");
					for (int j = 0; j < strategy[i].length; j++) {
						if (null != strategy[i][j]) {
							logger.info("strat["+i+"]["+j+"] - "+Arrays.toString(strategy[i][j]));
						}
					}
				} else {
					logger.info("["+i+"] is null");
				}
			}
			s.close();
		} else {
			logger.info("No file ["+fileName+"] so do it the hard way");
			return null;
		}
		return strategy;
	}
	
	private static void writeFile(String name, int accuracy, double effectiveStacks, double[][][] strategy) throws Exception {
		String fileName = createStratFileName(name, accuracy, effectiveStacks);
		ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(fileName));
		s.writeObject(strategy);
		s.close();
	}
	
	public static BaseNode getStrategyTree(BaseNode tree, String name, int accuracy, int effectiveStacks, EquityFunction ef, MonteCarloMachine monte) throws Exception {
		double strategy[][][] = null;
		double dummyStrat[][][] = null;
		double dummyRegret[][][] = null;
		
		BaseNode node = null;
		
		strategy = getStrategy(tree, name, accuracy, effectiveStacks, ef, monte);
		
		if (null != strategy) {
			node= tree.deepCopy(null);
			dummyStrat = new double[tree.getNodeCount()][][];
			dummyRegret = new double[tree.getNodeCount()][][];
			node.initStratAndRegret(dummyRegret, dummyStrat);
			node.getStratAndRegret(dummyRegret, dummyStrat);
			for (int i = 0; i < strategy.length; i++) {
				if (null != strategy[i]) {
					logger.info("j Length ["+strategy[i].length+"]");
					for (int j = 0; j < strategy[i].length; j++) {
						if (null != strategy[i][j]) {
							logger.info("strat["+i+"]["+j+"] - "+Arrays.toString(strategy[i][j]));
						}
					}
				} else {
					logger.info("["+i+"] is null");
				}
			}
			node.setStratAndRegret(dummyRegret, strategy, 0);
			node.setStratAndRegret(dummyRegret, strategy, 1);
		}
		
		return node;
	}
	
	public static double[][][] getStrategy(BaseNode tree, String name, int accuracy, double effectiveStacks, EquityFunction ef, MonteCarloMachine monte) throws Exception {
		int[] chanceIdxs;
		double strategy[][][] = null;
		
		strategy = checkFile(name, accuracy, effectiveStacks);
		if (null != strategy) {
			return strategy;
		}
		
		BaseNode champ = tree.deepCopy(null);
		BaseNode chall = tree.deepCopy(null);
		double champRegret[][][] = new double[champ.getNodeCount()][][];
		double champStrats[][][] = new double[champ.getNodeCount()][][];
		double challRegret[][][] = new double[chall.getNodeCount()][][];
		double challStrats[][][] = new double[chall.getNodeCount()][][];
		double regret[][][] = new double[tree.getNodeCount()][][];
		strategy = new double[tree.getNodeCount()][][];
		champ.initStratAndRegret(champRegret,champStrats);
		chall.initStratAndRegret(challRegret,challStrats);
		champ.getStratAndRegret(champRegret,champStrats);
		chall.getStratAndRegret(challRegret,challStrats);
		chall.initStratAndRegret(regret,strategy);
		int jmaxSeed = 1;
		int jmax = jmaxSeed;
		int same = 0;
		double mult = 1.25;
		double heroChallStrt,heroChallTed,villChallStrt,villChallTed;
		double challExploit = 0, challHeroTed = 0, challVillTed = 0;
		double champExploit = 10000;
		double[] stratVals;
		double[] heroStratVals = new double[monte.getHeroStratSize()];
		double[] villStratVals = new double[monte.getVillStratSize()];
		double[] heroExpStratVals = new double[monte.getHeroStratSize()];
		double[] villExpStratVals = new double[monte.getVillStratSize()];
		double[] heroTedStratVals = new double[monte.getHeroStratSize()];
		double[] villTedStratVals = new double[monte.getVillStratSize()];
		double target = 1.0/Math.pow(10, accuracy);
		
//		champ.printDescendants();
		
		while (true) {
			logger.info("Start Wash - "+(new Date())+", jmax - "+jmax);
			for (int j = 0; j < jmax; j++) {
				monte.reset();
				System.out.println(j+" of "+jmax);
				for (int i = 0; i < monte.getFlatArraySize(); i++) {
					chanceIdxs = monte.extractRandomMatch();
//					logger.debug("hero="+hs.pairs[0].toString()+",vill="+hs.pairs[1].toString());
					//TODO need to fix the first three params below
					chall.wash(1,1,0, chanceIdxs, ef, true, true);
					if (i%100000 == 0) System.out.println(i+" of "+monte.getFlatArraySize());
				}
			}
			logger.info("Finish Wash - "+(new Date()));
			
			//get strat pair and strats
//			stratVals = chall.getStratVals(hs,hupfr.heroRange,hupfr.villRange,heroStratVals,villStratVals);
			stratVals = chall.getStratValues(heroStratVals, villStratVals, monte.getHeroFreqs(), monte.getVillFreqs(), monte, ef);
			heroChallStrt = stratVals[0];villChallStrt = stratVals[1];
			chall.getStratAndRegret(challRegret,challStrats);		
			
			//get chall exploited
			chall.setExploitStrat(0);
			chall.setStratAndRegret(challRegret,challStrats,1);
//			stratVals = chall.getStratVals(hs,hupfr.heroRange,hupfr.villRange,heroExpStratVals,villTedStratVals);
			stratVals = chall.getStratValues(heroExpStratVals,villTedStratVals, monte.getHeroFreqs(), monte.getVillFreqs(), monte, ef);
			villChallTed = stratVals[1];
			chall.setStratAndRegret(challRegret,challStrats,0);			
			
			chall.setExploitStrat(1);
			chall.setStratAndRegret(challRegret,challStrats,0);
//			stratVals = chall.getStratVals(hs,hupfr.heroRange,hupfr.villRange,heroTedStratVals,villExpStratVals);
			stratVals = chall.getStratValues(heroTedStratVals,villExpStratVals, monte.getHeroFreqs(), monte.getVillFreqs(), monte, ef);
			heroChallTed = stratVals[0];
			chall.setStratAndRegret(challRegret,challStrats,1);
			
			challHeroTed = (heroChallStrt - heroChallTed);
			challVillTed = (villChallStrt - villChallTed);
			challExploit =  challHeroTed + challVillTed;
			
			if (challExploit < 0) {
				System.err.println("This should not be possible to have negative exploitation, Pure does not equal max exploit???");
				System.err.println("challheroExploit="+challHeroTed+",heroChallStrt="+heroChallStrt+",heroChallExpt="+heroChallTed);
				System.err.println("challvillExploit="+challVillTed+",villChallStrt="+villChallStrt+",villChallExpt="+villChallTed);
				System.exit(42);
			} else if (challExploit < (target)) {
				logger.info("Target "+target+" acheived - "+challExploit);
				chall.getStratAndRegret(regret,strategy);
				break;
			} else if (challExploit < champExploit) {
				same = 0;jmax = jmaxSeed;
				logger.info("Challenge success "+challExploit+" < "+champExploit);
				champ.setStratAndRegret(challRegret, challStrats, 0);
				champ.setStratAndRegret(challRegret, challStrats, 1);
				champExploit = challExploit;
			} else if (challExploit > champExploit) {
				same = 0;
				logger.info("Challenge fails "+challExploit+" > "+champExploit);
				jmax *= mult;
				jmax += 1;
				chall.setStratAndRegret(champRegret, champStrats, 0);
				chall.setStratAndRegret(champRegret, champStrats, 1);
			} else {
				jmax *= mult;
				jmax += 1;
				same++;
			}
			
			logger.info("Looping - "+(new Date()));
			if (same == 10) {
				logger.info("same time 10 - "+same);
				System.exit(42);
			}
		}
		for (int i = 0; i < 10; i++) {
			if (challExploit < (1.0/Math.pow(10, i))) {
				writeFile(name, i, effectiveStacks, strategy);
			}
		}
		//Perfection
		if (challExploit == 0) {
			writeFile(name, 99, effectiveStacks, strategy);
		}
		return strategy;
	}
}