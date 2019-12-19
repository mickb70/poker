/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameplay;

/**
 *
 * @author mick
 */
public class GameAction {
    private String name;
    private ActionType actionType;
    private double chips;
    private int chanceSize;
    
	public GameAction(String name, ActionType actionType, double chips, boolean preFlop) {
		super();
		this.name = name;
		this.actionType = actionType;
		this.chips = chips;
	
		if ((actionType == ActionType.Bet)||(actionType == ActionType.Call)||(actionType == ActionType.Check)
				||(actionType == ActionType.Fold)||(actionType == ActionType.Raise)) {
			this.chanceSize = (preFlop) ? 169 : 1326;
		} else {
			this.chanceSize = 1;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public double getChips() {
		return chips;
	}

	public void setChips(double chips) {
		this.chips = chips;
	}

	public int getChanceSize() {
		return chanceSize;
	}

	public void setChanceSize(int chanceSize) {
		this.chanceSize = chanceSize;
	}

	public boolean isEqualTo(GameAction gameAction) {
		if ( (this.actionType != gameAction.actionType) 
				|| (!this.name.startsWith(gameAction.name))
				|| (this.chips != gameAction.chips) ) {
			return false;
		} else {
			return true;
		}
	}
}