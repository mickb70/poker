package omaha;

import junit.framework.Assert;
import junit.framework.TestCase;
import omaha.Card;
import omaha.Hand;
import omaha.Rank;
import omaha.Suit;

public class OmahaEquityCalculatorTest extends TestCase {

    public void testNuts() {
        Hand isFlush = new Hand();
        Hand noFlush = new Hand();
		Hand board = new Hand();
		Hand[] fourCardHands = new Hand[2];
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Diamonds).ordinal);
		
		noFlush.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		board.addCard(Card.get(Rank.Queen, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		
		fourCardHands[0] = isFlush;
		fourCardHands[1] = noFlush;
		
		double[] equities = null;
		try {
			equities = OmahaEquityCalculator.flopEquities(fourCardHands, board);
		} catch (DeckInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(1, equities[0], .000001);
		Assert.assertEquals(0, equities[1], .000001);
    }
    
    public void testFlushDraw() {
        Hand isFlush = new Hand();
        Hand noFlush = new Hand();
		Hand board = new Hand();
		Hand[] fourCardHands = new Hand[2];
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Diamonds).ordinal);
		
		noFlush.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		board.addCard(Card.get(Rank.Queen, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Clubs).ordinal);
		
		fourCardHands[0] = isFlush;
		fourCardHands[1] = noFlush;
		
		double[] equities = null;
		try {
			equities = OmahaEquityCalculator.flopEquities(fourCardHands, board);
		} catch (DeckInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(0.668902, equities[0], .000001);
		Assert.assertEquals(0.331098, equities[1], .000001);
    }
    
    public void testThreeWayFlop() {
        Hand isFlush = new Hand();
        Hand noFlush = new Hand();
        Hand stFlush = new Hand();
		Hand board = new Hand();
		Hand[] fourCardHands = new Hand[3];
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Diamonds).ordinal);
		
		noFlush.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		stFlush.addCard(Card.get(Rank.Nine, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Diamonds).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Diamonds).ordinal);
		
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Diamonds).ordinal);
		
		fourCardHands[0] = isFlush;
		fourCardHands[1] = noFlush;
		fourCardHands[2] = stFlush;
		
		double[] equities = null;
		try {
			equities = OmahaEquityCalculator.flopEquities(fourCardHands, board);
		} catch (DeckInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(0.518018, equities[0], .000001);
		Assert.assertEquals(0.190691, equities[1], .000001);
		Assert.assertEquals(0.291291, equities[2], .000001);
    }
    
    public void testThreeWayTurn() {
        Hand isFlush = new Hand();
        Hand noFlush = new Hand();
        Hand stFlush = new Hand();
		Hand board = new Hand();
		Hand[] fourCardHands = new Hand[3];
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Diamonds).ordinal);
		
		noFlush.addCard(Card.get(Rank.Ace, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		stFlush.addCard(Card.get(Rank.Nine, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Hearts).ordinal);
		stFlush.addCard(Card.get(Rank.Nine, Suit.Diamonds).ordinal);
		stFlush.addCard(Card.get(Rank.Eight, Suit.Diamonds).ordinal);
		
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Four, Suit.Diamonds).ordinal);
		board.addCard(Card.get(Rank.Deuce, Suit.Clubs).ordinal);
		
		fourCardHands[0] = isFlush;
		fourCardHands[1] = noFlush;
		fourCardHands[2] = stFlush;
		
		double[] equities = null;
		try {
			equities = OmahaEquityCalculator.turnEquities(fourCardHands, board);
		} catch (DeckInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(0.472222, equities[0], .000001);
		Assert.assertEquals(0.305556, equities[1], .000001);
		Assert.assertEquals(0.222222, equities[2], .000001);
    }
    
    public void testDodgyDeck() {
        Hand isFlush = new Hand();
        Hand noFlush = new Hand();
		Hand board = new Hand();
		Hand[] fourCardHands = new Hand[2];
		
		OmahaEvaluator.initialize();
		
		isFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Hearts).ordinal);
		isFlush.addCard(Card.get(Rank.Ace, Suit.Diamonds).ordinal);
		isFlush.addCard(Card.get(Rank.King, Suit.Diamonds).ordinal);
		
		noFlush.addCard(Card.get(Rank.Ace, Suit.Hearts).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Clubs).ordinal);
		noFlush.addCard(Card.get(Rank.Ace, Suit.Spades).ordinal);
		noFlush.addCard(Card.get(Rank.King, Suit.Spades).ordinal);
		
		board.addCard(Card.get(Rank.Queen, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Jack, Suit.Hearts).ordinal);
		board.addCard(Card.get(Rank.Ten, Suit.Hearts).ordinal);
		
		fourCardHands[0] = isFlush;
		fourCardHands[1] = noFlush;
		
		double[] equities = null;
		try {
			equities = OmahaEquityCalculator.flopEquities(fourCardHands, board);
		} catch (DeckInvalidException e) {
			return;
		}
		
		Assert.fail(equities.toString());
    }
}
