package gametree;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import nash.HandRank;
import nash.PairRank;
import nash.RiverStrategy;
import spears2p2.*;
import util.Utilities;
import junit.framework.Assert;
import junit.framework.TestCase;

public class GameTreeTest extends TestCase {
	private static Logger logger = Logger.getLogger(GameTreeTest.class);
	
	private GameTree getRainBowNutLowRiverCcOrCfOrBetSubTree() {
		Hand board = new Hand();	
		board.addCard(Card.get(Rank.Deuce, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Five, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Clubs).ordinal);
		
		BoardNode boardNode = new BoardNode(board, BoardNodeType.River);
		
		double[] payOffsChkChkSD = {1,1};
		double potChkChkSD = 1;
		double[] payOffsChkBetFoldPO = {1,2};
		double[] payOffsChkBetCllSD = {0,0};
		double potChkBetCllSD = 3;
		double[] payOffsBetFoldPO = {2,1};
		double[] payOffsBetCllSD = {0,0};
		double potBetCllSD = 3;
		
		return GameTree.getRiverCcOrCfOrBetSubTree(boardNode, payOffsChkChkSD, potChkChkSD, payOffsChkBetFoldPO, payOffsChkBetCllSD, potChkBetCllSD, payOffsBetFoldPO, payOffsBetCllSD, potBetCllSD);
	}
	
	private GameTree getRainBowNutLowRiverChkOrBetSubTree() {
		Hand board = new Hand();	
		board.addCard(Card.get(Rank.Deuce, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Five, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Clubs).ordinal);
		
		BoardNode boardNode = new BoardNode(board, BoardNodeType.River);
		
		double betShowDownPayoffs[] = {0,0};
		double betShowDownPot = 3;
		double checkShowDownPayoffs[] = {1,1};
		double checkShowDownPot = 1;
		double foldPayoffs[] = {2,1};
		
		return GameTree.getRiverChkOrBetSubTree(boardNode, checkShowDownPayoffs, checkShowDownPot, foldPayoffs, betShowDownPayoffs, betShowDownPot);
	}
	
	private GameTree getFullHouseRiverChkOrBetSubTree() {
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
		
		return GameTree.getRiverChkOrBetSubTree(boardNode, checkShowDownPayoffs, checkShowDownPot, foldPayoffs, betShowDownPayoffs, betShowDownPot);
	}
	
	public void testStrategyCopy() {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		Pair redAs = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair redKs = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair redQs = Pair.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.Queen, Suit.Diamonds));
		Pair blkAs = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair blkKs = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair blkQs = Pair.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.Queen, Suit.Spades));
		
		double[][] freqs = new double[2][1326];
		double[][] betAll = new double[1326][2];
		double[][] callAll = new double[1326][2];
		
		freqs[0][redAs.ordinal] = 1;
		freqs[0][redKs.ordinal] = 1;
		freqs[0][redQs.ordinal] = 1;
		freqs[1][blkAs.ordinal] = 1;
		freqs[1][blkKs.ordinal] = 1;
		freqs[1][blkQs.ordinal] = 1;

		betAll[redAs.ordinal][0] = 1;
		betAll[redKs.ordinal][0] = 1;
		betAll[redQs.ordinal][0] = 1;
		callAll[blkAs.ordinal][1] = 1;
		callAll[blkKs.ordinal][0] = 0.9;
		callAll[blkKs.ordinal][1] = 0.1;
		callAll[blkQs.ordinal][0] = 0.5;
		callAll[blkQs.ordinal][1] = 0.5;
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(betAll);
		tree.getRoot().getKids()[1].setStrats(callAll);
		
		int heroIdx = 0;
		int villIdx = 1;
		
		double[][][] heroStrats = tree.getStrats(heroIdx);
		double[][][] villStrats = tree.getStrats(villIdx);
		
		Assert.assertEquals(1, heroStrats.length);
		Assert.assertEquals(1, villStrats.length);
		Assert.assertEquals(1326, heroStrats[0].length);
		Assert.assertEquals(2, heroStrats[0][redKs.ordinal].length);
		
		Assert.assertEquals(0.9, villStrats[0][blkKs.ordinal][0]);
		Assert.assertEquals((double)1, heroStrats[0][redKs.ordinal][0]);
		
		heroStrats[0][redKs.ordinal][0] = 0.5;
		heroStrats[0][redKs.ordinal][1] = 0.5;
		
		tree.writeStrats(heroStrats, heroIdx);
		
		double[][][] newHeroStrats = tree.getStrats(heroIdx);
		
		Assert.assertEquals((double)0.5, newHeroStrats [0][redKs.ordinal][0]);
	}
	
	public void testInitialiseAllStrats() {
		GameTree tree = getFullHouseRiverChkOrBetSubTree();
		
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
	
	public void testCheckAllRiverChkOrBetSubTreeGameValue() {
		GameTree tree = getFullHouseRiverChkOrBetSubTree();
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
	
	public void testRandomRiverChkOrBetSubTreeGameValue() {
		GameTree tree = getFullHouseRiverChkOrBetSubTree();
		
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
		
		Assert.assertEquals((double)1.625, tree.getGameValues()[0],.0000001);
		Assert.assertEquals((double)1.375, tree.getGameValues()[1],.0000001);
	}
	
	public void testRandomRiverCcOrCfOrBetSubTreeGameValue() {
		GameTree tree = getRainBowNutLowRiverCcOrCfOrBetSubTree();
		
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
		
		Assert.assertEquals((double)1.5625, tree.getGameValues()[0],.0000001);
		Assert.assertEquals((double)1.4375, tree.getGameValues()[1],.0000001);
	}
	
	public void testRiverGameValueNarrowRanges() {
		GameTree tree = getFullHouseRiverChkOrBetSubTree();
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
	
	public void testDeepCopyTree() {
		GameTree tree = getFullHouseRiverChkOrBetSubTree();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		double[][] freqs = new double[2][1326];
		double[][] checkAllStrats = new double[1326][2];
		
		Arrays.fill(freqs[0], 0);
		Arrays.fill(freqs[1], 0);

		freqs[0][aces.ordinal] = 1;
		
		for (Pair pair: Pair.values()) {
			if (!tree.getRoot().getBoardNode().getBoard().intersects(pair)) {
				freqs[1][pair.ordinal] = 1;
				checkAllStrats[pair.ordinal][0] = 1;
				checkAllStrats[pair.ordinal][1] = 0;
			}
		}
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		tree.getRoot().setStrats(checkAllStrats);
		
		GameTree copy = tree.deepCopy();
		
		Assert.assertEquals(copy.getAdjFreqs()[0][12], tree.getAdjFreqs()[0][12]);
		Assert.assertEquals(copy.getFreqs()[1][67], tree.getFreqs()[1][67]);
	}
	
	public void testRiverExploitCall() throws TreeInvalidException {
		GameTree tree = getFullHouseRiverChkOrBetSubTree();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair kingTrey = Pair.get(Card.get(Rank.King, Suit.Spades), Card.get(Rank.Three, Suit.Spades));
		Pair kings = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		
		double[][] freqs = new double[2][1326];
		double[][] betAllStrats = new double[1326][2];
		double[][] callHalfStrats = new double[1326][2];
		
		Arrays.fill(freqs[0], 0);
		Arrays.fill(freqs[1], 0);

		freqs[0][aces.ordinal] = 1;
		freqs[0][kings.ordinal] = 0.1;
		
		for (Pair pair: Pair.values()) {
			betAllStrats[pair.ordinal][0] = 0;
			betAllStrats[pair.ordinal][1] = 1;
			if (!tree.getRoot().getBoardNode().getBoard().intersects(pair)) {
				freqs[1][pair.ordinal] = 1;
				callHalfStrats[pair.ordinal][0] = 0.5;
				callHalfStrats[pair.ordinal][1] = 0.5;
			}
		}
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(betAllStrats);
		tree.getRoot().getKids()[1].setStrats(callHalfStrats);
		
		tree.setBestResponse(1);
		
		Assert.assertEquals((double)0, tree.getRoot().getKids()[1].getStrats()[aces.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getKids()[1].getStrats()[aces.ordinal][1]);
		
		Assert.assertEquals((double)0, tree.getRoot().getKids()[1].getStrats()[kingTrey.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getKids()[1].getStrats()[kingTrey.ordinal][1]);
	}
	
	public void testTreeValidation() {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();

		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		double[][] freqs = new double[2][1326];
		double[][] smallOnly = new double[1326][2];
		
		freqs[0][aces.ordinal] = 1;

		smallOnly[aces.ordinal][0] = 0;
		smallOnly[aces.ordinal][1] = 0.5;
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(smallOnly);
		
		try {
			tree.validateTree();
			fail("did not throw exception");
		} catch (TreeInvalidException e) {
		}
	}
	
	public void testRiverExploitBet() throws TreeInvalidException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		Pair redAs = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair redKs = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair redQs = Pair.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.Queen, Suit.Diamonds));
		Pair blkAs = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair blkKs = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair blkQs = Pair.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.Queen, Suit.Spades));
		
		double[][] freqs = new double[2][1326];
		double[][] betAll = new double[1326][2];
		double[][] callAll = new double[1326][2];
		
		freqs[0][redAs.ordinal] = 1;
		freqs[0][redKs.ordinal] = 1;
		freqs[0][redQs.ordinal] = 1;
		freqs[1][blkAs.ordinal] = 1;
		freqs[1][blkKs.ordinal] = 1;
		freqs[1][blkQs.ordinal] = 1;

		betAll[redAs.ordinal][0] = 1;
		betAll[redKs.ordinal][0] = 1;
		betAll[redQs.ordinal][0] = 1;
		callAll[blkAs.ordinal][1] = 1;
		callAll[blkKs.ordinal][0] = 0.9;
		callAll[blkKs.ordinal][1] = 0.1;
		callAll[blkQs.ordinal][0] = 0.5;
		callAll[blkQs.ordinal][1] = 0.5;
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(betAll);
		tree.getRoot().getKids()[1].setStrats(callAll);
		
		tree.setBestResponse(0);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redAs.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redAs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redKs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redKs.ordinal][1]);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redQs.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redQs.ordinal][1]);
	}
	
	public void testRiverFindValueRange() throws TreeInvalidException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		Pair redAs = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair redKs = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair redQs = Pair.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.Queen, Suit.Diamonds));
		Pair blkAs = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair blkKs = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair blkQs = Pair.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.Queen, Suit.Spades));
		
		double[][] freqs = new double[2][1326];
		double[][] betAll = new double[1326][2];
		double[][] callAll = new double[1326][2];
		
		freqs[0][redAs.ordinal] = 1;
		freqs[0][redKs.ordinal] = 1;
		freqs[0][redQs.ordinal] = 1;
		freqs[1][blkAs.ordinal] = 1;
		freqs[1][blkKs.ordinal] = 1;
		freqs[1][blkQs.ordinal] = 1;

		betAll[redAs.ordinal][0] = 1;
		betAll[redKs.ordinal][0] = 1;
		betAll[redQs.ordinal][0] = 1;
		callAll[blkAs.ordinal][1] = 1;
		callAll[blkKs.ordinal][0] = 0.9;
		callAll[blkKs.ordinal][1] = 0.1;
		callAll[blkQs.ordinal][0] = 0.5;
		callAll[blkQs.ordinal][1] = 0.5;
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(betAll);
		tree.getRoot().getKids()[1].setStrats(callAll);
		
		//remove bluff value
		tree.getRoot().removeBluffValue(tree.getRoot().getKids()[0]);
		
		tree.setBestResponse(0);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redAs.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redAs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redKs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redKs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redQs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redQs.ordinal][1]);
	}
	
	public void testGuessBetStrategy() throws TreeInvalidException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		Pair redAs = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair redKs = Pair.get(Card.get(Rank.King, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair redQs = Pair.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.Queen, Suit.Diamonds));
		Pair redJs = Pair.get(Card.get(Rank.Jack, Suit.Hearts), Card.get(Rank.Jack, Suit.Diamonds));
		Pair blkAs = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair blkKs = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair blkQs = Pair.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.Queen, Suit.Spades));
		Pair blkJs = Pair.get(Card.get(Rank.Jack, Suit.Clubs), Card.get(Rank.Jack, Suit.Spades));
		
		double[][] freqs = new double[2][1326];
		double[][] betAll = new double[1326][2];
		double[][] callMixed = new double[1326][2];
		
		freqs[0][redAs.ordinal] = 1;
		freqs[0][redKs.ordinal] = 1;
		freqs[0][redQs.ordinal] = 1;
		freqs[0][redJs.ordinal] = 1;
		freqs[1][blkAs.ordinal] = 1;
		freqs[1][blkKs.ordinal] = 1;
		freqs[1][blkQs.ordinal] = 1;
		freqs[1][blkJs.ordinal] = 1;

		betAll[redAs.ordinal][0] = 1;
		betAll[redKs.ordinal][0] = 1;
		betAll[redQs.ordinal][0] = 1;
		betAll[redJs.ordinal][0] = 1;
		callMixed[blkAs.ordinal][1] = 1;
		callMixed[blkKs.ordinal][0] = (double)2/3;
		callMixed[blkKs.ordinal][1] = (double)1/3;
		callMixed[blkQs.ordinal][0] = 1;
		callMixed[blkJs.ordinal][0] = 1;
		
		tree.setFreqs(freqs);
		tree.getRoot().setStrats(betAll);
		tree.getRoot().getKids()[1].setStrats(callMixed);
		
		//remove bluff value
		tree.getRoot().removeBluffValue(tree.getRoot().getKids()[0]);
		
		tree.setBestResponse(0);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redAs.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redAs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redKs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redKs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redQs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redQs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redJs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redJs.ordinal][1]);
		
		double[][] newBetStrats = tree.getRoot().getStrats();
		
		RiverStrategy.calcBluffRange(1, newBetStrats, tree.getAdjFreqs()[0], tree.getRoot().getBoardNode().getPairRankSets());
		
		tree.getRoot().setStrats(newBetStrats);
		
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redAs.ordinal][0]);
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redAs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redKs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redKs.ordinal][1]);
		
		Assert.assertEquals((double)1, tree.getRoot().getStrats()[redQs.ordinal][0]);
		Assert.assertEquals((double)0, tree.getRoot().getStrats()[redQs.ordinal][1]);
		
		Assert.assertEquals((double)1/2, tree.getRoot().getStrats()[redJs.ordinal][0]);
		Assert.assertEquals((double)1/2, tree.getRoot().getStrats()[redJs.ordinal][1]);		
	}
	
	public void testGuessBetStrategyCantBluff() throws TreeInvalidException {

	}

	public void testGuessingStrategyNarrowRange() throws TreeInvalidException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		GameTree noBluff = getRainBowNutLowRiverChkOrBetSubTree();
		
		double[][] freqs = new double[2][1326];
		
		for (int i = 8; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = j+1; k < 4; k++) {
					Card card1 = Card.get(i, j);
					Card card2 = Card.get(i, k);
					freqs[0][Pair.get(card1, card2).ordinal] =  1;
					freqs[1][Pair.get(card1, card2).ordinal] =  1;
				}
			}
		}
		
		tree.setFreqs(freqs);
		noBluff.setFreqs(freqs);
		tree.initialiseAllStrats();
		noBluff.initialiseAllStrats();
		
		//guess calling strategy
		double[][] callStrats = new double[1326][2];
		double[] callFreqs = tree.getAdjFreqs()[1];
		int nutsRank = tree.getRoot().getBoardNode().getNutsRank();
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = tree.getRoot().getBoardNode().getPairRankSets();
		RiverStrategy.calcCallOrFold(1, callStrats, callFreqs, nutsRank, pairRankSets);
		
		tree.getRoot().getKids()[1].setStrats(callStrats);
		noBluff.getRoot().getKids()[1].setStrats(callStrats);

		noBluff.getRoot().removeBluffValue(noBluff.getRoot().getKids()[0]);		
		noBluff.setBestResponse(0);
		
		double[][] newBetStrats = noBluff.getRoot().getStrats();
		RiverStrategy.calcBluffRange(1, newBetStrats, noBluff.getAdjFreqs()[0], noBluff.getRoot().getBoardNode().getPairRankSets());
		tree.getRoot().setStrats(newBetStrats);
		
		double exploit = Utilities.getDoubleArrayTotal(tree.getStratExploitability());
		
		Assert.assertEquals(0, exploit, .05);
	}
	
	public void testTreeGuessNarrowRangeNoCardRep() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		double goalExp = .000005;
		
		double[][] freqs = new double[2][1326];
		
		for (int i = 0; i < 12; i+=2) {
			Card card1 = Card.get(i, 0);
			Card card2 = Card.get(i, 1);
			Card card3 = Card.get(i, 2);
			Card card4 = Card.get(i, 3);
//			Card card3 = Card.get(i+1, 2);
//			Card card4 = Card.get(i+1, 3);
			freqs[0][Pair.get(card1, card2).ordinal] =  1;
			freqs[1][Pair.get(card3, card4).ordinal] =  1;
		}
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		
		GameTree copy = tree.findNashEqChkOrBetSubTree(1, goalExp, 10);
		
		double exploit = Utilities.getDoubleArrayTotal(copy.getStratExploitability());
		
		Assert.assertEquals(0, exploit, goalExp);
	}
	
	public void testTreeGuessNarrowRange() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		double goalExp = .000005;
		
		double[][] freqs = new double[2][1326];
		
		for (int i = 8; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = j+1; k < 4; k++) {
					Card card1 = Card.get(i, j);
					Card card2 = Card.get(i, k);
					freqs[0][Pair.get(card1, card2).ordinal] =  1;
					freqs[1][Pair.get(card1, card2).ordinal] =  1;
				}
			}
		}
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		
		GameTree copy = tree.findNashEqChkOrBetSubTree(1,goalExp, 10);
		
		double exploit = Utilities.getDoubleArrayTotal(copy.getStratExploitability());
		
		Assert.assertEquals(0, exploit, goalExp);
	}
	
	public void testGuessingStrategyFullRange() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		double goalExp = .005;
		
		double[][] freqs = new double[2][1326];
		
		Arrays.fill(freqs[0], 1);
		Arrays.fill(freqs[1], 1);
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		
		GameTree copy = tree.findNashEqChkOrBetSubTree(1, goalExp, 10);
		
		double exploit = Utilities.getDoubleArrayTotal(copy.getStratExploitability());
		
		Assert.assertEquals(0, exploit, goalExp);
	}
	
	public void testGuessingStrategyFullRandom() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverChkOrBetSubTree();
		double goalExp = .005;
		
		double[][] freqs = new double[2][1326];
		
		Arrays.fill(freqs[0], Math.random());
		Arrays.fill(freqs[1], Math.random());
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		
		GameTree copy = tree.findNashEqChkOrBetSubTree(1, goalExp, 10);
		
		double exploit = Utilities.getDoubleArrayTotal(copy.getStratExploitability());
		
		Assert.assertEquals(0, exploit, goalExp);
	}

	public void testRemoveBluffValue() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverCcOrCfOrBetSubTree();
		
		Assert.assertEquals(9, tree.getNumberNodes());
		
		tree.getRoot().removeBluffValue(tree.getRoot().getKids()[1].getKids()[0]);
		
		Assert.assertEquals(5, tree.getNumberNodes());
	}
	
	public void testTreeGuessRiverCcOrCfOrBetSubTreeNarrowRangeNoCardRep() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverCcOrCfOrBetSubTree();
		double goalExp = .006;
		
		double[][] freqs = new double[2][1326];
		
		for (int i = 6; i < 13; i++) {
			Card card1 = Card.get(i, 0);
			Card card2 = Card.get(i, 1);
			Card card3 = Card.get(i, 2);
			Card card4 = Card.get(i, 3);
			freqs[0][Pair.get(card1, card2).ordinal] =  1;
			freqs[1][Pair.get(card3, card4).ordinal] =  1;
		}
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		
		GameTree copy = tree.findNashEqRiverCcOrCfOrBetSubTree(1, goalExp, 10, 0.250001);
		
		double exploit = Utilities.getDoubleArrayTotal(copy.getStratExploitability());
		
		Assert.assertEquals(0, exploit, goalExp);
	}
	
	public void testTreeGuessRiverCcOrCfOrBetSubTreeFullRange() throws TreeInvalidException, NotSolvedException {
		GameTree tree = getRainBowNutLowRiverCcOrCfOrBetSubTree();
		double goalExp = .01;
		

		double[][] freqs = new double[2][1326];
		
		Arrays.fill(freqs[0], 1);
		Arrays.fill(freqs[1], 1);
		
		tree.setFreqs(freqs);
		tree.initialiseAllStrats();
		
		GameTree copy = tree.findNashEqRiverCcOrCfOrBetSubTree(1, goalExp, 10, .4);
		
		double exploit = Utilities.getDoubleArrayTotal(copy.getStratExploitability());
		
		Assert.assertEquals(0, exploit, goalExp);
	}
}
