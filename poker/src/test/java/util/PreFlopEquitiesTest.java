package util;

import junit.framework.Assert;
import junit.framework.TestCase;
import spears2p2.Card;
import spears2p2.Pair;
import spears2p2.Rank;
import spears2p2.Suit;

public class PreFlopEquitiesTest extends TestCase {
	
	public void testTwoDimesCompare() {
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Clubs));
		Pair kings = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Clubs));
		double eq = PreFlopEquities.equity[aces.isoOrd][kings.isoOrd];
		Assert.assertEquals(81.95, eq, 0.001);
	}

}
