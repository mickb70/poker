package nash;

import gametree.BoardNode;
import gametree.BoardNodeType;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import spears2p2.Card;
import spears2p2.Hand;
import spears2p2.Pair;
import spears2p2.Rank;
import spears2p2.Suit;
import junit.framework.Assert;
import junit.framework.TestCase;

public class RiverStrategyTest extends TestCase {
	
	public void testRiverCallHalfTime() {
		double betSize = 1;
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		TreeSet<PairRank> pairRankSet0 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet1 = new TreeSet<PairRank>();
		PairRank pairRank00 = new PairRank(0, 100);
		PairRank pairRank10 = new PairRank(1, 1);
		pairRankSet0.add(pairRank00);
		pairRankSet1.add(pairRank10);
		pairRankSets.put(new HandRank(100), pairRankSet0);
		pairRankSets.put(new HandRank(1), pairRankSet1);		
		double[] callStrats = {0,0};
		double[] callFreqs = {1,1};
		int nutsRank = 100;
		
		
		
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1.0, callStrats[0]);
		Assert.assertEquals((double)0.0, callStrats[1]);
	}
	
	public void testRiverCallHalfMixedStrat() {
		double betSize = 1;
		double[] callStrats = {0,0,0};
		double[] callFreqs = {1,1,1};
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		TreeSet<PairRank> pairRankSet0 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet1 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet2 = new TreeSet<PairRank>();
		PairRank pairRank00 = new PairRank(0, 100);
		PairRank pairRank10 = new PairRank(1, 50);
		PairRank pairRank20 = new PairRank(2, 1);
		pairRankSet0.add(pairRank00);
		pairRankSet1.add(pairRank10);
		pairRankSet2.add(pairRank20);
		pairRankSets.put(new HandRank(100), pairRankSet0);
		pairRankSets.put(new HandRank(50), pairRankSet1);
		pairRankSets.put(new HandRank(1), pairRankSet2);	
		int nutsRank = 100;
		
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1.0, callStrats[0]);
		Assert.assertEquals((double)0.5, callStrats[1]);
		Assert.assertEquals((double)0.0, callStrats[2]);
	}
	
	public void testRiverCallHalfMixedStratButNutsCall() {
		double betSize = 1;
		double[] callStrats = {0,0,0};
		double[] callFreqs = {1,1,1};
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		TreeSet<PairRank> pairRankSet0 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet1 = new TreeSet<PairRank>();
		PairRank pairRank00 = new PairRank(0, 100);
		PairRank pairRank01 = new PairRank(1, 100);
		PairRank pairRank10 = new PairRank(2, 1);
		pairRankSet0.add(pairRank00);
		pairRankSet0.add(pairRank01);
		pairRankSet1.add(pairRank10);
		pairRankSets.put(new HandRank(100), pairRankSet0);
		pairRankSets.put(new HandRank(1), pairRankSet1);
		int nutsRank = 100;
		
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1.0, callStrats[0]);
		Assert.assertEquals((double)1.0, callStrats[1]);
		Assert.assertEquals((double)0.0, callStrats[2]);
	}
	
	public void testRiverCallThirdMixedStrat() {
		double betSize = 2;
		double[] callStrats = {0,0,0,0};
		double[] callFreqs = {1,1,1,1};
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		TreeSet<PairRank> pairRankSet0 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet1 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet2 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet3 = new TreeSet<PairRank>();
		PairRank pairRank00 = new PairRank(0, 100);
		PairRank pairRank10 = new PairRank(1, 75);
		PairRank pairRank20 = new PairRank(2, 50);
		PairRank pairRank30 = new PairRank(3, 1);
		pairRankSet0.add(pairRank00);
		pairRankSet1.add(pairRank10);
		pairRankSet2.add(pairRank20);
		pairRankSet3.add(pairRank30);
		pairRankSets.put(new HandRank(100), pairRankSet0);
		pairRankSets.put(new HandRank(75), pairRankSet1);
		pairRankSets.put(new HandRank(50), pairRankSet2);
		pairRankSets.put(new HandRank(1), pairRankSet3);	
		int nutsRank = 100;
		
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1.0, callStrats[0]);
		Assert.assertEquals((double)1/3, callStrats[1], .000001);
		Assert.assertEquals((double)0.0, callStrats[2]);
		Assert.assertEquals((double)0.0, callStrats[3]);
	}
	
	public void testRiverCallTwoThirdsMixedStrat() {
		double betSize = 0.5;
		double[] callStrats = {0,0,0,0};
		double[] callFreqs = {1,1,1,1};
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		TreeSet<PairRank> pairRankSet0 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet1 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet2 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet3 = new TreeSet<PairRank>();
		PairRank pairRank00 = new PairRank(0, 100);
		PairRank pairRank10 = new PairRank(1, 75);
		PairRank pairRank20 = new PairRank(2, 50);
		PairRank pairRank30 = new PairRank(3, 1);
		pairRankSet0.add(pairRank00);
		pairRankSet1.add(pairRank10);
		pairRankSet2.add(pairRank20);
		pairRankSet3.add(pairRank30);
		pairRankSets.put(new HandRank(100), pairRankSet0);
		pairRankSets.put(new HandRank(75), pairRankSet1);
		pairRankSets.put(new HandRank(50), pairRankSet2);
		pairRankSets.put(new HandRank(1), pairRankSet3);	
		int nutsRank = 100;
		
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1.0, callStrats[0]);
		Assert.assertEquals((double)1.0, callStrats[1], .000001);
		Assert.assertEquals((double)2/3, callStrats[2], .000001);
		Assert.assertEquals((double)0.0, callStrats[3]);
	}
	
	public void testRiverCallTwoThirdsMultipeCalls() {
		double betSize = 0.5;
		double[] callStrats = {0,0,0,0};
		double[] callFreqs = {1,1,1,1};
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		TreeSet<PairRank> pairRankSet0 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet1 = new TreeSet<PairRank>();
		TreeSet<PairRank> pairRankSet2 = new TreeSet<PairRank>();
		PairRank pairRank00 = new PairRank(0, 100);
		PairRank pairRank10 = new PairRank(1, 75);
		PairRank pairRank11 = new PairRank(2, 75);
		PairRank pairRank20 = new PairRank(3, 1);
		pairRankSet0.add(pairRank00);
		pairRankSet1.add(pairRank10);
		pairRankSet1.add(pairRank11);
		pairRankSet2.add(pairRank20);
		pairRankSets.put(new HandRank(100), pairRankSet0);
		pairRankSets.put(new HandRank(75), pairRankSet1);
		pairRankSets.put(new HandRank(1), pairRankSet2);	
		int nutsRank = 100;
		
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1.0, callStrats[0]);
		Assert.assertEquals((double)5/6, callStrats[1], .000001);
		Assert.assertEquals((double)5/6, callStrats[2], .000001);
		Assert.assertEquals((double)0.0, callStrats[3]);
	}
	
	public void testRiverBoardCall() {
		Hand board = new Hand();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair dueces = Pair.get(Card.get(Rank.Deuce, Suit.Hearts), Card.get(Rank.Deuce, Suit.Diamonds));
		
		board.addCard(Card.get(Rank.Three, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Deuce, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Deuce, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		
		BoardNode node = new BoardNode(board, BoardNodeType.River);		

		double betSize = 1;
		double[] callFreqs = new double[1326];
		double[] callStrats = new double[1326];
		
		for (Pair pair: Pair.values()) {
			if (board.intersects(pair)) {
				callFreqs[pair.ordinal] = 0;
			} else {
				callFreqs[pair.ordinal] = 1;
			}
		}
		
		Arrays.fill(callStrats, 0);
		
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = node.getPairRankSets();
		int nutsRank = node.getNutsRank();
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank , pairRankSets);
		
		Assert.assertEquals((double)1, callStrats[aces.ordinal]);
		Assert.assertEquals((double)1, callStrats[dueces.ordinal]);
	}
	
	public void testRiverBoardCallMassiveOverBet() {
		Hand board = new Hand();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair treys = Pair.get(Card.get(Rank.Three, Suit.Hearts), Card.get(Rank.Three, Suit.Diamonds));
		
		board.addCard(Card.get(Rank.Deuce, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		
		BoardNode node = new BoardNode(board, BoardNodeType.River);		

		double betSize = 720.66;
		double[] callFreqs = new double[1326];
		double[] callStrats = new double[1326];
		
		for (Pair pair: Pair.values()) {
			if (board.intersects(pair)) {
				callFreqs[pair.ordinal] = 0;
			} else {
				callFreqs[pair.ordinal] = 1;
			}
		}
		
		Arrays.fill(callStrats, 0);
		
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = node.getPairRankSets();
		int nutsRank = node.getNutsRank();
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank , pairRankSets);
		
		Assert.assertEquals((double)1, callStrats[aces.ordinal]);
		Assert.assertEquals((double)0.5, callStrats[treys.ordinal],.01);
	}
	
	public void testRiverBoardCallAnyAce() {
		Hand board = new Hand();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair aceKing = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.King, Suit.Diamonds));
		Pair aceFive = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Five, Suit.Diamonds));
		Pair treys = Pair.get(Card.get(Rank.Three, Suit.Hearts), Card.get(Rank.Three, Suit.Diamonds));
		
		board.addCard(Card.get(Rank.Deuce, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		
		BoardNode node = new BoardNode(board, BoardNodeType.River);		

		double betSize = 44.1224;
		double[] callFreqs = new double[1326];
		double[] callStrats = new double[1326];
		
		for (Pair pair: Pair.values()) {
			if (board.intersects(pair)) {
				callFreqs[pair.ordinal] = 0;
			} else {
				callFreqs[pair.ordinal] = 1;
			}
		}
		
		Arrays.fill(callStrats, 0);
		
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = node.getPairRankSets();
		int nutsRank = node.getNutsRank();
		RiverStrategy.calcCallOrFold(betSize, callStrats, callFreqs, nutsRank , pairRankSets);
		
		Assert.assertEquals((double)1, callStrats[aces.ordinal]);
		Assert.assertEquals((double)1, callStrats[treys.ordinal]);
		Assert.assertEquals((double).25, callStrats[aceKing.ordinal],.01);
		Assert.assertEquals((double).25, callStrats[aceFive.ordinal],.01);
	}
	
	public void testRiverBoardBetMassiveOverBet() {
		Hand board = new Hand();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair treys = Pair.get(Card.get(Rank.Three, Suit.Hearts), Card.get(Rank.Three, Suit.Diamonds));
		
		board.addCard(Card.get(Rank.Deuce, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		
		BoardNode node = new BoardNode(board, BoardNodeType.River);		

		double betSize = 720.66;
		double[] callFreqs = new double[1326];
		double[] callStrats = new double[1326];
		double[] betFreqs = new double[1326];
		double[] betStrats = new double[1326];
		
		for (Pair pair: Pair.values()) {
			if (board.intersects(pair)) {
				callFreqs[pair.ordinal] = 0;
				betFreqs[pair.ordinal] = 0;
			} else {
				callFreqs[pair.ordinal] = 1;
				betFreqs[pair.ordinal] = 1;
			}
		}
		
		Arrays.fill(callStrats, 0);
		Arrays.fill(betStrats, 0);
		
		TreeMap<HandRank, TreeSet<PairRank>> pairRankSets = node.getPairRankSets();
		int nutsRank = node.getNutsRank();
		RiverStrategy.calcBetOrCheckPair(betSize, betStrats, betFreqs, callStrats, callFreqs, nutsRank, pairRankSets);
		
		Assert.assertEquals((double)1, betStrats[aces.ordinal]);
		Assert.assertEquals((double)0, betStrats[treys.ordinal],.01);
	}
}
