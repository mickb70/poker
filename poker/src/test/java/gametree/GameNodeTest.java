package gametree;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

public class GameNodeTest extends TestCase {
	//changed on pc

	public void testGetNumberNodes() {
		double betShowDownPayoffs[] = {0,0};
		double betShowDownPot = 3;
		double checkShowDownPayoffs[] = {1,1};
		double checkShowDownPot = 1;
		double foldPayoffs[] = {1,2};
		double emptyArr[] = {};
		
		double[][] allOnes = new double[2][1326];
		double[][] allZeros = new double[2][1326];
		
		for (int i = 0; i < allOnes.length; i++) {
			Arrays.fill(allOnes[i], 1);
		}
		
		for (int i = 0; i < allZeros.length; i++) {
			Arrays.fill(allZeros[i], 0);
		}
		
//		GameNode nodeCheckPayoff = new GameNode(null, null, GameNodeType.Showdown, checkShowDownPayoffs, checkShowDownPot, 0);
//		GameNode nodeBetFoldPayoff = new GameNode(null,null,GameNodeType.Payoff, foldPayoffs, 0, 0);
//		GameNode nodeBetCallPayoff = new GameNode(null,null,GameNodeType.Showdown, betShowDownPayoffs, betShowDownPot, 0);
//		GameNode nodeCallOrFoldChoice = new GameNode(null,null,GameNodeType.Action, emptyArr, 0, 2);
//		GameNode nodeCheckOrBetChoice = new GameNode(null,null,GameNodeType.Action, emptyArr, 0, 2);
//		
//		nodeCheckOrBetChoice.addGameTreeNode(0, nodeCheckPayoff);
//		nodeCheckOrBetChoice.addGameTreeNode(1, nodeCallOrFoldChoice);
//		nodeCallOrFoldChoice.addGameTreeNode(0, nodeBetFoldPayoff);
//		nodeCallOrFoldChoice.addGameTreeNode(1, nodeBetCallPayoff);
//		
//		Assert.assertEquals(5, nodeCheckOrBetChoice.getNumberNodes());
	}
}
