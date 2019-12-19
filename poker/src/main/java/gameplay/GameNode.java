package gameplay;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import util.Sequence;


public class GameNode {
	private static Logger logger = Logger.getLogger(GameNode.class);
	
	private final GameNode parent;
	private final int depth;
	private final GameAction gameAction;
	private final int idx;
	private ArrayList<GameNode> kids;
	private int visitCount;
	
	private int chanceVisits[];

	public GameNode(GameNode parent, GameAction gameAction, Sequence seq) {
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
		this.idx = seq.getNext();
		this.chanceVisits = new int[gameAction.getChanceSize()];
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

	public int[] getChanceVisits() {
		return chanceVisits;
	}

	public void setChanceVisits(int[] chanceVisits) {
		this.chanceVisits = chanceVisits;
	}

	public void visit(int chanceIdx) {
		this.chanceVisits[chanceIdx]++;
		this.visitCount++;
	}
	
	public GameNode getNode(int idx) {
		GameNode node = null;
		
		if (this.idx == idx) {
			return this;
		}
		
		for (GameNode kid: this.kids) {
			node = kid.getNode(idx);
			if (null != node) {
				return node;
			}
		}
		
		return node;
	}

	public GameNode addChild(GameAction gameAction, int chanceIdx, Sequence seq) {
		for (GameNode kid: this.kids) {
			if (kid.gameAction.isEqualTo(gameAction)) {
				System.out.println("her" + gameAction.getActionType());
				kid.visit(chanceIdx);
				return kid;
			}
		}
		
		GameNode newChild = new GameNode(this, gameAction, seq);
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
	
	public void printDescendants() {
		for (GameNode kid: this.kids) {
			kid.printDescendants();
		}
		logger.info(depth+",idx="+idx+",parent="+((parent == null)?-1:parent.idx));
	}
}
