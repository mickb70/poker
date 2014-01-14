package gametree;

public class ActionTree {
	private ActionNode root;
	
	public ActionTree() {
		
	}
	
	public static ActionTree getRiverCheckOrBetSubTree() {
		ActionTree tree = new ActionTree();
		
		ActionNode nodeCheckPayoff = new ActionNode(ActionNodeType.Payoff);
		ActionNode nodeBetFoldPayoff = new ActionNode(ActionNodeType.Payoff);
		ActionNode nodeBetCallPayoff = new ActionNode(ActionNodeType.Payoff);
		ActionNode nodeCallOrFoldChoice = new ActionNode(2,ActionNodeType.PlayerChoice);
		ActionNode nodeCheckOrBetChoice = new ActionNode(2,ActionNodeType.PlayerChoice);
		
		nodeCheckOrBetChoice.addGameTreeNode(0, nodeCheckPayoff);
		nodeCheckOrBetChoice.addGameTreeNode(1, nodeCallOrFoldChoice);
		nodeCallOrFoldChoice.addGameTreeNode(0, nodeBetFoldPayoff);
		nodeCallOrFoldChoice.addGameTreeNode(1, nodeBetCallPayoff);
		
		tree.root = nodeCheckOrBetChoice;
		
		return tree;
	}

	public int getNumberNodes() {
		return root.getNumberNodes();
	}
}
