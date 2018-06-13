package omaha;

import junit.framework.Assert;
import junit.framework.TestCase;
import omaha.Card;
import omaha.Hand;
import omaha.Rank;
import omaha.Suit;

public class OmahaEvaluatorTest extends TestCase {

    public void testOneCardFlush() {
        Hand isFlush = new Hand();
        Hand noFlush = new Hand();
		Hand board = new Hand();
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		noFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		board.addCard(Card.get(Rank.Deuce, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Three, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Five, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Clubs).ordinal);
		
		int isFlushRank = OmahaEvaluator.getRank(isFlush, board);
		int noFlushRank = OmahaEvaluator.getRank(noFlush, board);

		Assert.assertTrue(isFlushRank > noFlushRank);
    }
    
    public void testStaightFlush() {
        Hand isFlush = new Hand();
        Hand stFlush = new Hand();
		Hand board = new Hand();
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		stFlush.addCard(Card.get(Rank.Eight, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Diamonds).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Diamonds).ordinal);
		
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Hearts).ordinal);
		
		int isFlushRank = OmahaEvaluator.getRank(isFlush, board);
		int stFlushRank = OmahaEvaluator.getRank(stFlush, board);

		Assert.assertTrue(stFlushRank > isFlushRank);
    }
    
    public void testStaightFlushOverSF() {
        Hand isFlush = new Hand();
        Hand stFlush = new Hand();
		Hand board = new Hand();
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		stFlush.addCard(Card.get(Rank.Eight, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Diamonds).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Diamonds).ordinal);
		
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Queen, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Hearts).ordinal);
		
		int isFlushRank = OmahaEvaluator.getRank(isFlush, board);
		int stFlushRank = OmahaEvaluator.getRank(stFlush, board);

		Assert.assertTrue(isFlushRank > stFlushRank);
    }
    
    public void testJackHighStaightFlush() {
        Hand stFlush = new Hand();
		Hand board = new Hand();
		
		OmahaEvaluator.initialize();
		
		stFlush.addCard(Card.get(Rank.Eight, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Diamonds).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Diamonds).ordinal);
		
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Spades).ordinal);
		board.addCard(Card.get(Rank.Seven, Suit.Hearts).ordinal);
		
		int stFlushRank = OmahaEvaluator.getRank(stFlush, board);

		Assert.assertEquals(7458, stFlushRank);
    }
}