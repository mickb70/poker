package spears2p2;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FlopTest extends TestCase {

	public void testTrips () {
		Flop flop1 = Flop.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Ace, Suit.Spades), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(1754, flop2.isoIdx);
		Assert.assertEquals(1755, Flop.isoOrdCount);
		assertTrue(flop1.ordinal != flop2.ordinal);
	}
	
	public void testSameTrips () {
		Flop flop1 = Flop.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		assertTrue(flop1.ordinal == flop2.ordinal);
	}

	public void testTranspose () {
		Flop flop1 = Flop.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Ace, Suit.Diamonds), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Clubs));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		assertTrue(flop1.ordinal == flop2.ordinal);
	}
	
	public void testLastFlop () {
		Flop flop1 = Flop.get(Card.get(Rank.Ace, Suit.Spades), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		
		Assert.assertEquals((Flop.count-1), flop1.ordinal);
	}
	
	public void testIsoFlop() {
		Flop flop1 = Flop.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Hearts), Card.get(Rank.Ace, Suit.Spades));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(flop1.isoOrdinal, flop2.isoOrdinal);
		assertTrue(flop1.ordinal != flop2.ordinal);
	}
	
	public void testMonotoneFlop() {
		Flop flop1 = Flop.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.King, Suit.Clubs), Card.get(Rank.Ace, Suit.Clubs));
		Flop flop2 = Flop.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.King, Suit.Hearts), Card.get(Rank.Ace, Suit.Hearts));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(flop1.isoOrdinal, flop2.isoOrdinal);
		assertTrue(flop1.ordinal != flop2.ordinal);
		assertTrue(flop1.ordinal == flop2.getIsoFlop().ordinal);
	}

	public void testFlushDrawFlop() {
		Flop flop1 = Flop.get(Card.get(Rank.Queen, Suit.Clubs), Card.get(Rank.King, Suit.Clubs), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.King, Suit.Hearts), Card.get(Rank.Ace, Suit.Clubs));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(flop1.isoOrdinal, flop2.isoOrdinal);
		assertTrue(flop1.ordinal != flop2.ordinal);
		assertTrue(flop1.ordinal == flop2.getIsoFlop().ordinal);
	}
	
	public void testNonClubFlushDrawFlop() {
		Flop flop1 = Flop.get(Card.get(Rank.Queen, Suit.Spades), Card.get(Rank.King, Suit.Spades), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Queen, Suit.Hearts), Card.get(Rank.King, Suit.Hearts), Card.get(Rank.Ace, Suit.Spades));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(flop1.isoOrdinal, flop2.isoOrdinal);
		assertTrue(flop1.ordinal != flop2.ordinal);
		assertTrue(flop1.getIsoFlop().ordinal == flop2.getIsoFlop().ordinal);
	}
	
	public void testRainbowFlop() {
		Flop flop1 = Flop.get(Card.get(Rank.Queen, Suit.Spades), Card.get(Rank.Nine, Suit.Clubs), Card.get(Rank.Ace, Suit.Diamonds));
		Flop flop2 = Flop.get(Card.get(Rank.Nine, Suit.Spades), Card.get(Rank.Ace, Suit.Diamonds), Card.get(Rank.Queen, Suit.Hearts));
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(flop1.isoOrdinal, flop2.isoOrdinal);
		assertTrue(flop1.ordinal != flop2.ordinal);
		assertTrue(flop1.getIsoFlop().ordinal == flop2.getIsoFlop().ordinal);
	}
	
	public void testTransposeRainbowFlop() {
		Flop flop1 = Flop.get(Card.get(Rank.Nine, Suit.Clubs), Card.get(Rank.Queen, Suit.Diamonds), Card.get(Rank.Ace, Suit.Hearts));
		Flop flop2 = Flop.get(Card.get(Rank.Nine, Suit.Clubs), Card.get(Rank.Queen, Suit.Spades), Card.get(Rank.Ace, Suit.Hearts));
		
		Pair pair1 = Pair.get(Card.get(Rank.King, Suit.Diamonds), Card.get(Rank.Ace, Suit.Diamonds));
		Pair pair2 = Pair.get(Card.get(Rank.King, Suit.Spades), Card.get(Rank.Ace, Suit.Spades));
		
		Flop flopTrans1 = flop1.getIsoFlop();
		Flop flopTrans2 = flop2.getIsoFlop();
		
		Pair pairTrans1 = flop1.transposePair(pair1);
		Pair pairTrans2 = flop2.transposePair(pair2);
		
		Assert.assertEquals(flop1.isoIdx, flop2.isoIdx);
		Assert.assertEquals(flop1.isoOrdinal, flop2.isoOrdinal);
		assertTrue(flopTrans1.ordinal == flopTrans2.ordinal);
		assertTrue(pair1.ordinal == pairTrans1.ordinal);
		assertTrue(pair1.ordinal == pairTrans2.ordinal);
		assertTrue(pair2.ordinal != pairTrans2.ordinal);
	}
}
