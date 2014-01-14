package gametree;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ActionNodeTest extends TestCase {

	public void testGetNumberNodes() {
		ActionNode nodeCheckPayoff = new ActionNode(ActionNodeType.Payoff);
		ActionNode nodeBetFoldPayoff = new ActionNode(ActionNodeType.Payoff);
		ActionNode nodeBetCallPayoff = new ActionNode(ActionNodeType.Payoff);
		ActionNode nodeCallOrFoldChoice = new ActionNode(2,ActionNodeType.PlayerChoice);
		ActionNode nodecheckOrBetChoice = new ActionNode(2,ActionNodeType.PlayerChoice);
		
		nodecheckOrBetChoice.addGameTreeNode(0, nodeCheckPayoff);
		nodecheckOrBetChoice.addGameTreeNode(1, nodeCallOrFoldChoice);
		nodeCallOrFoldChoice.addGameTreeNode(0, nodeBetFoldPayoff);
		nodeCallOrFoldChoice.addGameTreeNode(1, nodeBetCallPayoff);
		
		Assert.assertEquals(5, nodecheckOrBetChoice.getNumberNodes());
	}
}
