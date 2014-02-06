package gametree;


public class ActionNode {
	private ActionNode[] kids; 
	private ActionNodeType nodeType;
	
	public ActionNode(int numKids, ActionNodeType nodeType) {
		this.kids = new ActionNode[numKids];
		this.nodeType = nodeType; 
	}
	
	public ActionNode(ActionNodeType gameTreeNodeType) {
		this.kids = null;
		this.nodeType = gameTreeNodeType;
	}
	
	public void addGameTreeNode(int idx, ActionNode gameTreeNode) {
		this.kids[idx] = gameTreeNode;
	}
	
	public int getNumberNodes() {
		int numKids = 0;
		
		if (kids != null) {
			for (ActionNode kid: kids) {
				numKids += kid.getNumberNodes();
			}
		}
		
		return numKids + 1;
	}
}