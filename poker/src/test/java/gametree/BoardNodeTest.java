package gametree;

import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Assert;
import junit.framework.TestCase;
import nash.HandRank;
import nash.PairRank;
import spears2p2.Card;
import spears2p2.Hand;
import spears2p2.Pair;
import spears2p2.Rank;
import spears2p2.Suit;

public class BoardNodeTest extends TestCase {
	
	public void testHouseBoard() {
		Hand board = new Hand();
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Pair dueces = Pair.get(Card.get(0, 0), Card.get(0, 1));
		
		board.addCard(Card.get(Rank.Three, Suit.Clubs).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		
		BoardNode node = new BoardNode(board, BoardNodeType.River);
		
		//get top of list
		TreeMap<HandRank, TreeSet<PairRank>> pairRanksets = node.getPairRankSets();
		
		HandRank topHandRank = pairRanksets.firstKey();
		HandRank bottomHandRank = pairRanksets.lastKey();
		
		TreeSet<PairRank> topPairRankSet = pairRanksets.get(topHandRank);
		TreeSet<PairRank> bottomPairRankSet = pairRanksets.get(bottomHandRank);
		
		Assert.assertEquals(aces.ordinal, topPairRankSet.first().getOrdinal());
		Assert.assertEquals(dueces.ordinal, bottomPairRankSet.last().getOrdinal());
	}

}
