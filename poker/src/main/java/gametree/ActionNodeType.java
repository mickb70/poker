package gametree;

public enum ActionNodeType {
	PlayerChoice("player choice"),
	Chance("chance"),
	Payoff("payoff"),
	Deferred("deffered");
	
	private final String toString;
	
	private ActionNodeType(String toString) {
		this.toString = toString;
	}
	
	public String toString() {
		return toString;
	}
}
