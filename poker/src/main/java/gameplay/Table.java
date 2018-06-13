/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameplay;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author mick
 */
public class Table {
    private int buttonIdx;
    private double ante;
    private double smallBlind;
    private double bigBlind;
    private LinkedHashMap<String,Player> players;
    private LinkedHashMap<String,Pot> pots;
    private LinkedList<GameAction> actions;

    public Table(int buttonIdx, double ante, double smallBlind, double bigBlind) {
        this.buttonIdx = buttonIdx;
        this.ante = ante;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.players = new LinkedHashMap<String,Player>();
        this.pots = new LinkedHashMap<String,Pot>();
        this.actions = new LinkedList<GameAction>();
    }

	public int getButtonIdx() {
		return buttonIdx;
	}

	public void setButtonIdx(int buttonIdx) {
		this.buttonIdx = buttonIdx;
	}

	public double getAnte() {
		return ante;
	}

	public void setAnte(double ante) {
		this.ante = ante;
	}

	public double getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(double smallBlind) {
		this.smallBlind = smallBlind;
	}

	public double getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(double bigBlind) {
		this.bigBlind = bigBlind;
	}
	
	public void putPlayer(Player player) {
		this.players.put(player.getName(), player);
	}
	
	public Player getPlayer(String name) {
		return this.players.get(name);
	}

	public int getNumSeats() {
		return this.players.size();
	}

	public LinkedHashMap<String, Player> getPlayers() {
		return players;
	}

	public void setPlayers(LinkedHashMap<String, Player> players) {
		this.players = players;
	}

	public LinkedHashMap<String, Pot> getPots() {
		return pots;
	}

	public void setPots(LinkedHashMap<String, Pot> pots) {
		this.pots = pots;
	}

	public LinkedList<GameAction> getActions() {
		return actions;
	}

	public void setActions(LinkedList<GameAction> actions) {
		this.actions = actions;
	}
}
