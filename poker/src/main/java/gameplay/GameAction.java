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
    
	public GameAction(String name, ActionType actionType, double chips) {
		super();
		this.name = name;
		this.actionType = actionType;
		this.chips = chips;
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