package util;

import junit.framework.Assert;
import junit.framework.TestCase;
import spears2p2.Card;
import spears2p2.Pair;
import spears2p2.Rank;
import spears2p2.Suit;

public class HeadsUpRangeEquitiesTest extends TestCase {
	
	public void testTwoDimesCompare() {
		Pair aces = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.Ace, Suit.Spades));
		Pair kings = Pair.get(Card.get(Rank.King, Suit.Clubs), Card.get(Rank.King, Suit.Spades));
		Pair Aks = Pair.get(Card.get(Rank.Ace, Suit.Clubs), Card.get(Rank.King, Suit.Clubs));
		
		Pair dollyP = Pair.get(Card.get(Rank.Nine, Suit.Clubs), Card.get(Rank.Five, Suit.Spades));
		Pair twos = Pair.get(Card.get(Rank.Deuce, Suit.Clubs), Card.get(Rank.Deuce, Suit.Spades));
		
		double[] heroRange = new double[169];
		double[] villRange = new double[169];
		
		heroRange[twos.isoOrd] = 1;
		villRange[aces.isoOrd] = 1;
		villRange[kings.isoOrd] = 1;
		villRange[Aks.isoOrd] = 1;
		
		HeadsUpRangeEquites hure = new HeadsUpRangeEquites(heroRange, villRange);
		
		Assert.assertEquals(0.2602, hure.heroTotEqs, 0.0001);
		
		heroRange = new double[169];
		heroRange[dollyP.isoOrd] = 1;
		hure = new HeadsUpRangeEquites(heroRange, villRange);
		
		Assert.assertEquals(0.1875, hure.heroTotEqs, 0.0001);
		Assert.assertEquals(60976416, hure.heroTotWins, 0.1);
		Assert.assertEquals(1342128, hure.heroTotChop, 0.1);
		Assert.assertEquals(266443824, hure.heroTotLoss, 0.1);
		
		heroRange = new double[169];
		heroRange[twos.isoOrd] = 1;
		heroRange[dollyP.isoOrd] = 1;
		hure = new HeadsUpRangeEquites(heroRange, villRange);
		
		Assert.assertEquals(0.2117, hure.heroTotEqs, 0.0001);
		Assert.assertEquals(103276452, hure.heroTotWins, 0.1);
		Assert.assertEquals(0.2602, hure.heroRngEqs[twos.isoOrd], 0.0001);
		Assert.assertEquals(0.1875, hure.heroRngEqs[dollyP.isoOrd], 0.0001);
		
		heroRange = new double[169];
		villRange = new double[169];
		heroRange[dollyP.isoOrd] = 1;
		villRange[aces.isoOrd] = 1;
		hure = new HeadsUpRangeEquites(heroRange, villRange);
		Assert.assertEquals(17034336, hure.heroTotWins, 0.1);
		Assert.assertEquals(465804, hure.heroTotChop, 0.1);
		Assert.assertEquals(105785748, hure.heroTotLoss, 0.1);
	}
}
