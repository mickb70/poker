package ignition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import gameplay.GameNode;
import gameplay.Table;
import junit.framework.Assert;
import junit.framework.TestCase;

public class HandHistoryParserTest extends TestCase {
	
	public void testExtractHands() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> hands = HandHistoryParser.extractHands("resources/testHandHistory.txt", false);
		
		assertEquals(44, hands.size());
	}
	
	public void testExtractHandsFromDir() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> hands = 
				HandHistoryParser.extractHandsFromDir("resources/testHands","ZONE - $0.10-$0.25 - HOLDEM", false);
		
		Table table = hands.get("3593807264");
		Assert.assertEquals(7.93, table.getPlayer("Dealer").getChipStack(), 0.001);
		
		assertEquals(6231, hands.size());
	}
	
	public void testParseHandPreFlopOnly() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> hands = HandHistoryParser.extractHands("resources/testHandHistory.txt", false);
		
		Table table = hands.get("3593807264");
		Assert.assertEquals(24.90, table.getPlayer("UTG+1").getChipStack(), 0.001);
		
		assertNotNull(table);
		
		Assert.assertEquals("UTG+1", table.getPlayer("UTG+1").getName());
		Assert.assertEquals("BigBlind[ME]", table.getPlayer("BigBlind[ME]").getName());
		
		Assert.assertEquals(24.90, table.getPlayer("UTG+1").getChipStack(), 0.001);
		Assert.assertEquals(25.00, table.getPlayer("BigBlind[ME]").getChipStack(), 0.001);
		
		Assert.assertEquals("2c7d", table.getPlayer("UTG+1").getPair().toString());
		Assert.assertEquals("7cJs", table.getPlayer("BigBlind[ME]").getPair().toString());
		
		Assert.assertEquals(10, table.getActions().size());
	}
	
	public void testParseHandRiverNonShowdown() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> hands = HandHistoryParser.extractHands("resources/testHandHistory.txt", false);
		
		Table table = hands.get("3593807400");
		assertNotNull(table);
		
		Assert.assertEquals("UTG[ME]", table.getPlayer("UTG[ME]").getName());
		Assert.assertEquals("BigBlind", table.getPlayer("BigBlind").getName());
		
		Assert.assertEquals(24.75, table.getPlayer("UTG[ME]").getChipStack(), 0.001);
		Assert.assertEquals(16.34, table.getPlayer("BigBlind").getChipStack(), 0.001);
		
		Assert.assertEquals("3hQh", table.getPlayer("UTG[ME]").getPair().toString());
		
		Assert.assertEquals(16, table.getActions().size());
	} 

	
	public void testParseHandRiverShowdown() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> hands = HandHistoryParser.extractHands("resources/testHandHistory.txt", false);
		
		Table table = hands.get("3593807461");
		assertNotNull(table);
		
		Assert.assertEquals(33.11, table.getPlayer("Dealer").getChipStack(), 0.001);
		
		Assert.assertEquals("6c9c", table.getPlayer("Dealer").getPair().toString());
		
		Assert.assertEquals(17, table.getActions().size());
	}
	
	public void testCreatePreFlopTree() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> tables = 
				HandHistoryParser.extractHandsFromDir("resources/testHands","ZONE - $0.10-$0.25 - HOLDEM", true);
		
		GameNode tree = HandHistoryParser.createPreFlopTreeSixMin(tables);
		
		Assert.assertEquals(882,tree.getNumDescendants());
	}
	
	public void testCreatePreFlopTreeDlrVsBb() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> tables = 
				HandHistoryParser.extractHandsFromDir("resources/testHands","ZONE - $0.10-$0.25 - HOLDEM", true);
		
		GameNode tree = HandHistoryParser.createPreFlopTreeSixMinDlrVsBb(tables);
		
		Assert.assertEquals(103,tree.getNumDescendants());
	}
	
	public void testCreatePreFlopTreeDlrRFI3bb() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> tables = 
				HandHistoryParser.extractHandsFromDir("resources/testHands","ZONE - $0.10-$0.25 - HOLDEM", false);
		
		GameNode tree = HandHistoryParser.createPreFlopTreeSixMinDlrRFI3bb(tables);
		
		Assert.assertEquals(250,tree.getNumDescendants());
	}
	
	public void testCreatePreFlopTreeDlrMeEffStack11to14() throws FileNotFoundException, IOException, HandHistoryInvalidException {
		HashMap<String, Table> tables = 
				HandHistoryParser.extractHandsFromDir("resources/moreTestHH","minEffStackDlr11-14", false);
		double minEff = 11;
		double maxEff = 14;
		String player = "Dealer[ME]";
		
		GameNode tree = HandHistoryParser.createPreFlopTreeEff(tables,player,minEff,maxEff);
		
		Assert.assertEquals(5, tables.size());
		Assert.assertEquals(2, tree.getKids().get(0).getVisitCount());
	}
}
