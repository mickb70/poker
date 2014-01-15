package gametree;

import java.util.Arrays;

import spears2p2.*;
import junit.framework.Assert;
import junit.framework.TestCase;

public class GameTreeTest extends TestCase {
	
	private GameTree getRainBowTree() {
		Hand board = new Hand();	
		board.addCard(Card.get(Rank.Deuce, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Eight, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Clubs).ordinal);
		
		BoardNode boardNode = new BoardNode(board, BoardNodeType.River);
		
		double betShowDownPayoffs[] = {0,0};
		double betShowDownPot = 3;
		double checkShowDownPayoffs[] = {1,1};
		double checkShowDownPot = 1;
		double foldPayoffs[] = {2,1};
		
		return GameTree.getRiverCheckOrBetSubTree(boardNode, checkShowDownPayoffs, checkShowDownPot, foldPayoffs, betShowDownPayoffs, betShowDownPot);
	}
	
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
		double foldPayoffs[] = {2,1};
		
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
	
	public void testRiverExploitCall() throws TreeInvalidException {
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
	
	public void testTreeValidation() {
		GameTree tree = getRainBowTree();
		Pair smallest = Pair.values()[0];
		
		double[][] freqs = new double[2][1326];
		double[][] smallOnly = new double[1326][2];
		
		freqs[0][smallest.ordinal] = 1;

		smallOnly[smallest.ordinal][0] = 0;
		smallOnly[smallest.ordinal][1] = 0.5;
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(smallOnly);
		
		try {
			tree.validateTree();
			fail("did not throw exception");
		} catch (TreeInvalidException e) {
		}
	}
	
	public void testRiverStrategyFindValueRange() throws TreeInvalidException {
		GameTree tree = getRainBowTree();
		Pair redAs = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair redKs = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair redQs = Pair.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.Queen, Suit.Diamonds));
		Pair blkAs = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair blkKs = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair blkQs = Pair.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.Queen, Suit.Spades));
		
		double[][] freqs = new double[2][1326];
		double[][] callAsKsOnly = new double[1326][2];
		
		freqs[0][redAs.ordinal] = 1;
		freqs[0][redKs.ordinal] = 1;
		freqs[0][redQs.ordinal] = 1;
		freqs[1][blkAs.ordinal] = .9;
		freqs[1][blkKs.ordinal] = 1;
		freqs[1][blkQs.ordinal] = 1;

		callAsKsOnly[blkAs.ordinal][0] = 0;
		callAsKsOnly[blkKs.ordinal][0] = 0;
//		callAsKsOnly[blkQs.ordinal][0] = 0;
		callAsKsOnly[blkAs.ordinal][1] = 1;
		callAsKsOnly[blkKs.ordinal][1] = 1;
//		callAsKsOnly[blkQs.ordinal][1] = 1;
		
		tree.setFreqs(freqs);
		tree.getRoot().getKids()[1].setStrats(callAsKsOnly);
		
		tree.setBestResponse(0);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redAs.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redAs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redKs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redKs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redQs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redQs.ordinal][1]);
	}
}
