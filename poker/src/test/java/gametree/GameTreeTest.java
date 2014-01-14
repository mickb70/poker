package gametree;

import java.util.Arrays;

import spears2p2.*;
import junit.framework.Assert;
import junit.framework.TestCase;

public class GameTreeTest extends TestCase {
	
	private GameTree getFullHouseTree() {
		//changed on laptop again work this time
		Hand board = new Hand();	
		board.addCard(Card.get(Rank.Three, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		
		BoardNode boardNode = new BoardNode(board, BoardNodeType.River);
		
		double betShowDownPayoffs[] = {0,0};
		double betShowDownPot = 3;
		double checkShowDownPayoffs[] = {1,1};
		double checkShowDownPot = 1;
		double foldPayoffs[] = {1,2};
		
		return GameTree.getRiverCheckOrBetSubTree(boardNode, checkShowDownPayoffs, checkShowDownPot, foldPayoffs, betShowDownPayoffs, betShowDownPot);
	}
	
	public void testRiverSubGame() {
		GameTree tree = getFullHouseTree();
		
		double[][] freqs = new double[2][1326];
		
		Arrays.fill(freqs[0], 0);
		Arrays.fill(freqs[1], 0);
		
		for (Pair pair: Pair.values()) {
			if (!tree.getRoot().getBoardNode().getBoard().intersects(pair)) {
				freqs[0][pair.ordinal] = 1;
				freqs[1][pair.ordinal] = 1;
			}
		}
		
		tree.setFreqs(freqs);
		
		tree.initialiseAllStrats();
		
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		Assert.assertEquals(0.5, tree.getRoot().getStrats()[aces.ordinal][0]);
		Assert.assertEquals(0.5, tree.getRoot().getStrats()[aces.ordinal][1]);
	}
	
	public void testRiverGameValue() {
		GameTree tree = getFullHouseTree();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		double[][] freqs = new double[2][1326];
		double[][] checkAllStrats = new double[1326][2];
		
		Arrays.fill(freqs[0], 0);
		Arrays.fill(freqs[1], 0);
		
		for (Pair pair: Pair.values()) {
			checkAllStrats[pair.ordinal][0] = 1;
			checkAllStrats[pair.ordinal][1] = 0;
			if (!tree.getRoot().getBoardNode().getBoard().intersects(pair)) {
				freqs[0][pair.ordinal] = 1;
				freqs[1][pair.ordinal] = 1;
			}
		}
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(checkAllStrats);
		
		Assert.assertEquals((double)1.5, tree.getGameValues()[0],.0000001);
		Assert.assertEquals((double)1.5, tree.getGameValues()[1],.0000001);
		
		Assert.assertEquals((double)2, tree.getPairValues()[0][aces.ordinal]);
		Assert.assertEquals((double)2, tree.getPairValues()[1][aces.ordinal]);
	}
	
	public void testRiverGameValueNarrowRanges() {
		GameTree tree = getFullHouseTree();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		double[][] freqs = new double[2][1326];
		double[][] checkAllStrats = new double[1326][2];
		
		Arrays.fill(freqs[0], 0);
		Arrays.fill(freqs[1], 0);

		freqs[0][aces.ordinal] = 1;
		
		for (Pair pair: Pair.values()) {
			checkAllStrats[pair.ordinal][0] = 1;
			checkAllStrats[pair.ordinal][1] = 0;
			if (!tree.getRoot().getBoardNode().getBoard().intersects(pair)) {
				freqs[1][pair.ordinal] = 1;
			}
		}
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(checkAllStrats);
		
		Assert.assertEquals((double)2, tree.getGameValues()[0],.0000001);
		Assert.assertEquals((double)1, tree.getGameValues()[1],.0000001);
		
		Assert.assertEquals((double)2, tree.getPairValues()[0][aces.ordinal]);
		Assert.assertEquals((double)0, tree.getPairValues()[1][aces.ordinal]);
	}
	
	public void testRiverExploitCall() {
		GameTree tree = getFullHouseTree();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair kingTrey = Pair.get(Card.get(Rank.King, Suit.Spades), Card.get(Rank.Three, Suit.Spades));
		Pair kings = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		
		double[][] freqs = new double[2][1326];
		double[][] betAllStrats = new double[1326][2];
		
		Arrays.fill(freqs[0], 0);
		Arrays.fill(freqs[1], 0);

		freqs[0][aces.ordinal] = 1;
		freqs[0][kings.ordinal] = 0.1;
		
		for (Pair pair: Pair.values()) {
			betAllStrats[pair.ordinal][0] = 0;
			betAllStrats[pair.ordinal][1] = 1;
			if (!tree.getRoot().getBoardNode().getBoard().intersects(pair)) {
				freqs[1][pair.ordinal] = 1;
			}
		}
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(betAllStrats);
		
		tree.setBestResponse(1);
		
		Assert.assertEquals((double)0, tree.getRoot().getKids()[1].getStrats()[aces.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getKids()[1].getStrats()[aces.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getKids()[1].getStrats()[kingTrey.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getKids()[1].getStrats()[kingTrey.ordinal][1]);
	}
	
	public void testRiverStrategyFindValueRange() {
		GameTree tree = getFullHouseTree();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair kings = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair QTrey = Pair.get(Card.get(Rank.Jack, Suit.Spades), Card.get(Rank.Three, Suit.Spades));
		Pair JTrey = Pair.get(Card.get(Rank.Queen, Suit.Spades), Card.get(Rank.Three, Suit.Spades));
		
		double[][] freqs = new double[2][1326];
		double[][] callAcesOnly = new double[1326][2];
		
		freqs[0][aces.ordinal] = 1;freqs[0][kings.ordinal] = 1;freqs[0][QTrey.ordinal] = 1; freqs[0][JTrey.ordinal] = 1;
		freqs[1][aces.ordinal] = 1;freqs[1][kings.ordinal] = 1;freqs[1][QTrey.ordinal] = 1; freqs[1][JTrey.ordinal] = 1;

		callAcesOnly[aces.ordinal][0] = 0;callAcesOnly[kings.ordinal][0] = 0;callAcesOnly[QTrey.ordinal][0] = 1; callAcesOnly[JTrey.ordinal][0] = 1;
		callAcesOnly[aces.ordinal][1] = 1;callAcesOnly[kings.ordinal][1] = 1;callAcesOnly[QTrey.ordinal][1] = 0; callAcesOnly[JTrey.ordinal][1] = 0;
		
		tree.setFreqs(freqs);
		tree.getRoot().getKids()[1].setStrats(callAcesOnly);
		
		tree.setBestResponse(0);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[aces.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[aces.ordinal][1]);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[kings.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[kings.ordinal][1]);
	}
}
