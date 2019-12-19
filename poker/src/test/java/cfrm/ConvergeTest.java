package cfrm;

import java.util.Date;

import junit.framework.TestCase;

public class ConvergeTest extends TestCase {

	
	public void testNutsOrNothingRiver() throws Exception {
		double strategy[][][] = null;
		
		//This is node with option of all in bet or checkBack
		TreeIdx treeIdx = new TreeIdx();
		BaseNode tree = new BaseNode(2, "NutsOrNothing", treeIdx);

		tree.actIdx = 0;
		tree.depth = 0;
		tree.idx = treeIdx.idxSeq++;
		tree.pot = 1;
		tree.bets = new double[2];
		tree.bets[1] = 1;
		tree.kids = new BaseNode[2];
		
		//Hero decides not to bluff
		BaseNode checkBack = new BaseNode(0, "NutsOrNothing", treeIdx);
		tree.kids[0] = checkBack;
		
		checkBack.actIdx = BaseNode.SHOWDN;
		checkBack.depth = 1;
		checkBack.idx = treeIdx.idxSeq++;
		checkBack.pot = 1;
		
		//Hero value bets or bluffs
		BaseNode betOnEnd = new BaseNode(1, "NutsOrNothing", treeIdx);
		tree.kids[1] = betOnEnd;
		
		betOnEnd.actIdx = 1;
		betOnEnd.depth = 1;
		betOnEnd.idx = treeIdx.idxSeq++;
		betOnEnd.pot = 1;
		betOnEnd.bets = new double[2];
		betOnEnd.bets[1] = 1;
		betOnEnd.kids = new BaseNode[2];
		
		//villain folds
		BaseNode villFolds = new BaseNode(0, "NutsOrNothing", treeIdx);
		betOnEnd.kids[0] = villFolds;
		
		villFolds.actIdx = BaseNode.PAYOUT;
		villFolds.depth = 2;
		villFolds.idx = treeIdx.idxSeq++;
		villFolds.pot = 2;
		villFolds.payouts = new double[2];
		villFolds.payouts[1] = 2;

		//villain bluff catches
		BaseNode bluffCatch = new BaseNode(1, "NutsOrNothing", treeIdx);
		betOnEnd.kids[1] = bluffCatch;
		
		bluffCatch.actIdx = BaseNode.SHOWDN;
		bluffCatch.depth = 2;
		bluffCatch.idx = treeIdx.idxSeq++;
		bluffCatch.pot = 3;
		
		for (int i = 400; i < 2501; i+=50) {
			System.out.println("getting Strat - "+(new Date())+" - effective "+i);
			Converge.getStrategy(tree, "NutsOrNothing", 1, 1, new NutsOrNothingEF(), new NutsOrNothingMCM());
		}
		
		assertTrue(true);
	}

}
