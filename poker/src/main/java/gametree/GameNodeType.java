package gametree;

public enum GameNodeType {
	Action("action"),
	Chance("chance"),
	Payoff("payoff"),
	Showdown("showdown");
	
	private final String toString;
	
	private GameNodeType(String toString) {
		this.toString = toString;
	}
	
	public String toString() {
		return toString;
	}
}
