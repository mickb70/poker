package ignition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gameplay.ActionType;
import gameplay.GameAction;
import gameplay.GameNode;
import gameplay.Player;
import gameplay.Table;
import spears2p2.Card;
import spears2p2.Pair;

public class HandHistoryParser {
	private enum HandSection {
		Init,
		Deal,
		PreFlop,
		Flop,
		Turn,
		River,
		Showdown, 
		Complete
	}
	


	public static GameNode createPreFlopTreeEff(HashMap<String, Table> tables, String player, double minEff, double maxEff) throws HandHistoryInvalidException {
		GameNode tree = new GameNode(null, new GameAction("root", ActionType.Init, 0));
		GameNode currNode = tree;
		Table table = null;
		
		for (Entry<String, Table> tableEntry : tables.entrySet()) {
			table = tableEntry.getValue();
			currNode = tree;
			double effectiveStack = table.getEffectiveStackPlayerHasMin(player);
			
			if (table.getNumSeats() < 6) {
				continue;
			}
			
			if ((effectiveStack < minEff)||(effectiveStack > maxEff)) {
				continue;
			}
			
			for (GameAction action : table.getActions()) {
				if (action.getActionType() == ActionType.Flop) {
					break;
				} else if (action.getActionType() == ActionType.Turn) {
					throw new HandHistoryInvalidException("gone past the flop");
				}
				currNode = currNode.addChild(action);
			}
		}
		
		return tree;
	}
	
	public static GameNode createPreFlopTreeSixMin(HashMap<String, Table> tables) throws HandHistoryInvalidException {
		GameNode tree = new GameNode(null, new GameAction("root", ActionType.Init, 0));
		GameNode currNode = tree;
		Table table = null;
		
		for (Entry<String, Table> tableEntry : tables.entrySet()) {
			table = tableEntry.getValue();
			currNode = tree;
			
			if (table.getNumSeats() < 6) {
				break;
			}
			
			for (GameAction action : table.getActions()) {
				if (action.getActionType() == ActionType.Flop) {
					break;
				} else if (action.getActionType() == ActionType.Turn) {
					throw new HandHistoryInvalidException("gone past the flop");
				}
				currNode = currNode.addChild(action);
			}
		}
		
		return tree;
	}
	
	public static GameNode createPreFlopTreeSixMinDlrVsBb(HashMap<String, Table> tables) {
		GameNode tree = new GameNode(null, new GameAction("root", ActionType.Init, 0));
		GameNode currNode = tree;
		Table table = null;
		
		for (Entry<String, Table> tableEntry : tables.entrySet()) {
			table = tableEntry.getValue();
			currNode = tree;
			
			if (table.getNumSeats() < 6) {
				break;
			}
			
			for (GameAction action : table.getActions()) {
				if (action.getActionType() == ActionType.Flop) {
					break;
				} else if (action.getActionType() == ActionType.SmallBlind || action.getActionType() == ActionType.BigBlind) {
					
				} else if (action.getName().contains("Dealer") || action.getName().contains("BigBlind")) {
					//do nothing OK
				} else {
					if (action.getActionType() != ActionType.Fold) {
						break;
					}
				}
				currNode = currNode.addChild(action);
			}
		}
		
		return tree;
	}
	
	public static GameNode createPreFlopTreeSixMinDlrRFI3bb(HashMap<String, Table> tables) {
		GameNode tree = new GameNode(null, new GameAction("root", ActionType.Init, 0));
		GameNode currNode = tree;
		Table table = null;
		boolean DealerActed = false;
		
		for (Entry<String, Table> tableEntry : tables.entrySet()) {
			table = tableEntry.getValue();
			currNode = tree;
			
			if (table.getNumSeats() < 6) {
				break;
			}
			
			for (GameAction action : table.getActions()) {
				if (action.getActionType() == ActionType.Flop) {
					break;
				} else if (action.getActionType() == ActionType.SmallBlind || action.getActionType() == ActionType.BigBlind) {
					
				} else if (action.getName().contains("Dealer")) {
					if (!DealerActed) {
						if ((action.getActionType() == ActionType.Raise) && (action.getChips() == 0.75)) {
							DealerActed = true;
						} else {
							break;
						}
					}
				} else if (action.getName().contains("Blind")) {
					//do nothing OK
				} else {
					if (action.getActionType() != ActionType.Fold) {
						break;
					}
				}
				currNode = currNode.addChild(action);
			}
		}
		
		return tree;
	}
	
	public static HashMap<String, Table> extractHands (String fileName, boolean stripMe) throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, ArrayList<String>> hands = new HashMap<String,ArrayList<String>>();
		HashMap<String,Table> tables = new HashMap<String,Table>();
		Table table = null;
		Pattern patternDC = Pattern.compile("Ignition Hand #(\\d*)\\s(.*)");
		String handId = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    String line;
		    ArrayList<String> strArr = null;
		    
		    while ((line = br.readLine()) != null) {
		    	if (line.startsWith("Ignition Hand #")) {
		    		Matcher matcherDC = patternDC.matcher(line);
		    		if (matcherDC.matches()) {
		    			handId = matcherDC.group(1);
		    		} else {
		    			throw new HandHistoryInvalidException(line);
		    		}
		    		strArr = new ArrayList<String>();
		    		hands.put(handId, strArr);
		    	}
	    		strArr.add(line);
		    }
		}
		
		for (Map.Entry<String, ArrayList<String>> handEntry: hands.entrySet()) {
			table = parseHand(handEntry.getValue(), stripMe);
			tables.put(handEntry.getKey(), table);
		}
		
		return tables;
	}
	
	public static HashMap<String, Table> extractHandsFromDir(String dirName, String match, boolean stripMe) throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String,Table> tables = new HashMap<String,Table>();
		HashMap<String, Table> extraHands = null;
		
		File[] files = new File(dirName).listFiles(); 

		for (File file : files) {
		    if (file.isFile()) {
		    	if (file.getName().contains(match)) {
			        extraHands = extractHands(file.getPath(), stripMe);
			        tables.putAll(extraHands);
		    	}
		    }
		}
		
		return tables;
	}
	
	private static String getNameFromInit(String str, boolean stripMe) throws HandHistoryInvalidException {
		String name = null;
		String subStr = str.substring(str.indexOf(":")+1);
		
		Pattern patternDC = Pattern.compile("(.*?)\\(.*");
		Matcher matcherDC = patternDC.matcher(subStr);
		if (matcherDC.matches()) {
			if (stripMe) {
				name = new String(matcherDC.group(1)).replaceAll("\\s", "").replaceAll("\\[ME\\]", "");
			} else {
				name = new String(matcherDC.group(1)).replaceAll("\\s", "");
			}
		} else {
			throw new HandHistoryInvalidException(str); 
		}
		
		return name;
	}
	
	private static String getNameFromStartAction(String str, boolean stripMe) throws HandHistoryInvalidException {
		String name = null;
		
		Pattern patternDC = Pattern.compile("(.*?):.*");
		Matcher matcherDC = patternDC.matcher(str);
		if (matcherDC.matches()) {
			if (stripMe) {
				name = new String(matcherDC.group(1)).replaceAll("\\s", "").replaceAll("\\[ME\\]", "");
			} else {
				name = new String(matcherDC.group(1)).replaceAll("\\s", "");
			}
		} else {
			throw new HandHistoryInvalidException(str); 
		}
		
		return name;
	}
	
	private static double[] getCurrency(String str) {
		String[] currs = str.split("\\$");
		double[] dblCurrs = new double[currs.length - 1];
		
		
		for (int i = 1; i < currs.length; i++) {
			String strippedCurr = currs[i].replaceAll("[^0-9.]", "");
			dblCurrs[i-1] = Double.parseDouble(strippedCurr);
		}
		
		return dblCurrs;
	}
	
	private static void parsePayout(String str, Table table, boolean stripMe) throws HandHistoryInvalidException {
		String name = getNameFromStartAction(str, stripMe);
		double tot = getCurrency(str)[0];
		table.getActions().add(new GameAction(name, ActionType.Payout, tot));
	}
	
	private static void parseCall(String str, Table table, boolean stripMe) throws HandHistoryInvalidException {
		String name = getNameFromStartAction(str, stripMe);
		double call = getCurrency(str)[0];
		table.getActions().add(new GameAction(name, ActionType.Call, call));
	}

	private static void parseBet(String str, Table table, boolean stripMe) throws HandHistoryInvalidException {
		String name = getNameFromStartAction(str, stripMe);
		double bet = getCurrency(str)[0];
		table.getActions().add(new GameAction(name, ActionType.Bet, bet));
	}
	
	private static void parseRaise(String str, Table table, boolean stripMe) throws HandHistoryInvalidException {
		String name = getNameFromStartAction(str, stripMe);
		double bet = getCurrency(str)[0];
		table.getActions().add(new GameAction(name, ActionType.Raise, bet));
	}
	
	private static Player parsePlayerInit(String str, boolean stripMe) throws HandHistoryInvalidException {
		String name = getNameFromInit(str, stripMe);
		double stack = getCurrency(str)[0];
		return new Player(name, stack);
	}

	private static Table parseBlinds(String dealIdxStr, String sBStr, String bbStr, LinkedList<GameAction> tmpActions) throws HandHistoryInvalidException {
		int buttonIdx = 0;
		double smallBlind = 0;
		double bigBlind = 0;
		
		//Dealer : Set dealer [4]
		Pattern patternDB = Pattern.compile("Dealer(.*?): Set dealer \\S(\\d*)(.*)");
		Matcher matcherDB = patternDB.matcher(dealIdxStr);
		
		if (matcherDB.matches()) {
			buttonIdx = Integer.parseInt(matcherDB.group(2)) - 1;
		} else {
			throw new HandHistoryInvalidException(dealIdxStr);
		}
		
		smallBlind = getCurrency(sBStr)[0];
		tmpActions.add(new GameAction("SmallBlind", ActionType.SmallBlind, smallBlind));
		
		bigBlind = getCurrency(bbStr)[0];
		tmpActions.add(new GameAction("BigBlind", ActionType.BigBlind, bigBlind));
		
		return new Table(buttonIdx, 0, smallBlind, bigBlind);
	}
	
	private static void parseHandId(String str) {
		// TODO Auto-generated method stub
		
	}

	private static void parseFlop(String str, Table table) {
		// TODO Auto-generated method stub
		
	}

	private static void parseTurn(String str, Table table) {
		// TODO Auto-generated method stub
		
	}

	private static void parseRiver(String str, Table table) {
		// TODO Auto-generated method stub
		
	}

	private static void parseHoleCards(String str, Table table, boolean stripMe) throws HandHistoryInvalidException {
		String c1 = null;
		String c2 = null;
		String name = null;
		Player player = null;
		Pattern patternDC = Pattern.compile("(.*?):\\sCard dealt to a spot \\W(\\w*)\\s(\\w*).*");
		Matcher matcherDC = patternDC.matcher(str);
		if (matcherDC.matches()) {
			name = getNameFromStartAction(str, stripMe);
			c1 = new String(matcherDC.group(2));
			c2 = new String(matcherDC.group(3));
			player = table.getPlayer(name);
			if (null != player) {
				player.setPair(Pair.get(Card.parse(c1),Card.parse(c2)));
			} else {
				throw new HandHistoryInvalidException(name);
			}
		} else {
			throw new HandHistoryInvalidException(str);
		}
	}
	
	private static void parseBettingAction(String str, Table table, boolean stripMe) throws HandHistoryInvalidException {
		System.out.println(table.getActions().size()+","+str);
		if (str.contains("(Auto)")||str.contains("Does not show")) {
			//do nothing
		} else if (str.contains(": Folds")) {
			String name = getNameFromStartAction(str, stripMe);
			table.getActions().add(new GameAction(name, ActionType.Fold, 0));
		} else if (str.contains(": Checks")) {
			String name = getNameFromStartAction(str, stripMe);
			table.getActions().add(new GameAction(name, ActionType.Check, 0));
		} else if (str.contains(": Calls")) {
			parseCall(str, table, stripMe);
		} else if (str.contains(": Bets") || str.contains(": All-in $")) {
			parseBet(str, table, stripMe);
		} else if (str.contains(": Raises") || str.contains(": All-in(raise)")) {
			parseRaise(str, table, stripMe);
		} else if (str.contains("Return uncalled portion")||str.contains("Hand result")) {
			parsePayout(str, table, stripMe);
		} else if (str.contains(": Showdown") || str.contains(": Mucks")) {
			//TODO need to parse showdown
		} else {
			throw new HandHistoryInvalidException(str);
		}
	}

	public static Table parseHand(ArrayList<String> hand, boolean stripMe) throws HandHistoryInvalidException {
		String str = null;
		HandSection handSect = HandSection.Init;
	    LinkedHashMap<String,Player> tmpPlayers = new LinkedHashMap<String,Player>();
	    LinkedList<GameAction> tmpActions = new LinkedList<GameAction>();
		Table table = null;
		
		for (int i = 0; i < hand.size(); i++) {
			str = hand.get(i);
			if (str.startsWith("Ignition Hand #")) {
				parseHandId(str);
				handSect = HandSection.Init;
			} else if (str.startsWith("*** FLOP ***")) {
				parseFlop(str,table);
				handSect = HandSection.Flop;
			} else if (str.startsWith("*** TURN ***")) {
				parseTurn(str,table);
				handSect = HandSection.Turn;
			} else if (str.startsWith("*** RIVER ***")) {
				parseRiver(str,table);
				handSect = HandSection.River;
			} else if (str.startsWith("*** SUMMARY ***")) {
				handSect = HandSection.Complete;
			} else if (str.contains("Table enter user") || str.contains("Table deposit") 
					|| str.contains("Seat sit out") || str.contains("Seat re-join")
					|| str.contains("Seat stand") || str.contains("Table leave user")) {
				//do nothing
			} else if (handSect == HandSection.Init) {
				if (str.startsWith("Seat ")) {
					Player player = parsePlayerInit(str, stripMe); 
					tmpPlayers.put(player.getName(), player);
				} else if (str.startsWith("Dealer")) {
					table = parseBlinds(hand.get(i),hand.get(i+1),hand.get(i+2), tmpActions);
					i+=2;
				} else if (str.startsWith("*** HOLE CARDS ***")) {
					handSect = HandSection.Deal;
					for (Map.Entry<String, Player> player : tmpPlayers.entrySet()) {
						table.putPlayer(player.getValue());
					}
					for (GameAction action : tmpActions) {
						table.getActions().add(action);
					}
				}
			} else if (handSect == HandSection.Deal) {
				if (str.contains("Card dealt to a spot")) {
					parseHoleCards(str, table, stripMe);
				} else {
					handSect = HandSection.PreFlop;
					parseBettingAction(str, table, stripMe);
				}
			} else if ((handSect == HandSection.PreFlop) || (handSect == HandSection.Flop) 
					|| (handSect == HandSection.Turn) || (handSect == HandSection.River) ) {
				parseBettingAction(str, table, stripMe);;
			} else if (handSect == HandSection.Complete) {
				// do nothing
			} else {
				throw new HandHistoryInvalidException(str);
			}
		}
		
		return table;
	}
}
