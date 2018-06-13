package gameplay;

import java.util.ArrayList;

import org.apache.log4j.Logger;


public class GameNode {
	private static Logger logger = Logger.getLogger(GameNode.class);
	
	private final GameNode parent;
	private final int depth;
	private final GameAction gameAction;
	private ArrayList<GameNode> kids;
	private int visitCount;

	public GameNode(GameNode parent, GameAction gameAction) {
		super();
		this.parent = parent;
		
		if (this.parent != null) {
			this.depth = this.parent.depth + 1;
		} else {
			this.depth = 0;
		}
		
		this.gameAction = gameAction;
		this.kids = new ArrayList<GameNode>();
		this.visitCount = 1;
	}

	public ArrayList<GameNode> getKids() {
		return kids;
	}

	public void setKids(ArrayList<GameNode> kids) {
		this.kids = kids;
	}

	public GameAction getGameAction() {
		return gameAction;
	}

	public int getDepth() {
		return depth;
	}
	
	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public void visit() {
		this.visitCount++;
	}

	public GameNode addChild(GameAction gameAction) {
		for (GameNode kid: this.kids) {
			if (kid.gameAction.isEqualTo(gameAction)) {
				kid.visit();
				return kid;
			}
		}
		
		GameNode newChild = new GameNode(this, gameAction);
		this.kids.add(newChild);
		return newChild;
	}

	public int getNumDescendants() {
		int numDesc = 1;  //TODO is this right to include self
		
		for (GameNode kid: this.kids) {
			numDesc += kid.getNumDescendants();
			logger.debug(kid.gameAction+","+numDesc);
		}
		
		return numDesc;
	}
}
