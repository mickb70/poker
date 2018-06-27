package util;

import junit.framework.Assert;
import junit.framework.TestCase;
import spears2p2.Card;
import spears2p2.Pair;
import spears2p2.Rank;
import spears2p2.Suit;

public class HeadsUpPreFlopEquitiesTest extends TestCase {
	
	public void testTwoDimesCompare() {
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair kings = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair twos = Pair.get(Card.get(Rank.Deuce, Suit.Clubs), Card.get(Rank.Deuce, Suit.Spades));
		Pair Aks = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.King, Suit.Clubs));
		Pair dollyP = Pair.get(Card.get(Rank.Nine, Suit.Clubs), Card.get(Rank.Five, Suit.Spades));
		
		
		Assert.assertEquals(0.8195, HeadsUpPreFlopEquities.equity[aces.isoOrd][kings.isoOrd], 0.0001);
		Assert.assertEquals(0.8222, HeadsUpPreFlopEquities.equity[aces.isoOrd][twos.isoOrd], 0.0001);
		Assert.assertEquals(0.8786, HeadsUpPreFlopEquities.equity[aces.isoOrd][Aks.isoOrd], 0.0001);
		Assert.assertEquals(0.6589, HeadsUpPreFlopEquities.equity[kings.isoOrd][Aks.isoOrd], 0.0001);
		Assert.assertEquals(0.6589, HeadsUpPreFlopEquities.equity[kings.isoOrd][Aks.isoOrd], 0.0001);
		Assert.assertEquals(0.6765, HeadsUpPreFlopEquities.equity[Aks.isoOrd][dollyP.isoOrd], 0.0001);
	}

}
