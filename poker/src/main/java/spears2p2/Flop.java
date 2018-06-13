package spears2p2;

import java.util.BitSet;

public class Flop {
	public final static int isoCount = 1755;
	public final static int count = (Card.count * (Card.count - 1) * (Card.count -2)) / 6;
	
	private static Flop[] values = new Flop[count];
	public static Flop[] isoValues = new Flop[isoCount];
	private static Flop[][][] valuesByCard = new Flop[Card.count][Card.count][Card.count];
	private static boolean[][] intersectsPair  = new boolean[count][Pair.count];
	private static boolean[][] intersectsCard  = new boolean[count][Card.count];
	
	public static int isoOrdCount = 0;

	public static Flop get(Card c1, Card c2, Card c3) {
		return valuesByCard[c1.ordinal][c2.ordinal][c3.ordinal];
	}
	
	public static int getIndex(int i, int j, int k) {
		return valuesByCard[i][j][k].ordinal;
	}
	
	public final int ordinal;
	public final int isoOrdinal;
	public final int isoIdx;
	private final Suit[] suitTransArr;
	
	private final Card[] cards = new Card[3];
	
	public String toString() {
		return cards[0].toString + cards[1].toString + cards[2].toString;
	}
	
	public Card[] getCards() {
		return cards;
	}
	
	public boolean intersects(Pair p) {
		return intersectsPair[this.ordinal][p.ordinal];
	}

	public boolean intersects(Card c) {
		return intersectsCard[this.ordinal][c.ordinal];
	}

	private static void findPairIntersections() {
		for (Flop flop : values) {
			Card[] flopCards = flop.cards;
			int flopOrdinal = flop.ordinal;
			for (Pair pair : Pair.values) {
				int pairOrdinal = pair.ordinal;
				Card[] pairCards = pair.cards;
				
				intersectsPair[flopOrdinal][pairOrdinal] = false;
				
				if(flopCards[0].ordinal == pairCards[0].ordinal) intersectsPair[flopOrdinal][pairOrdinal] = true;
				if(flopCards[0].ordinal == pairCards[1].ordinal) intersectsPair[flopOrdinal][pairOrdinal] = true;
				if(flopCards[1].ordinal == pairCards[0].ordinal) intersectsPair[flopOrdinal][pairOrdinal] = true;
				if(flopCards[1].ordinal == pairCards[1].ordinal) intersectsPair[flopOrdinal][pairOrdinal] = true;
				if(flopCards[2].ordinal == pairCards[0].ordinal) intersectsPair[flopOrdinal][pairOrdinal] = true;
				if(flopCards[2].ordinal == pairCards[1].ordinal) intersectsPair[flopOrdinal][pairOrdinal] = true;
			}
		}
	}
	
	private static void findCardIntersections() {
		for (Flop flop : values) {
			Card[] flopCards = flop.cards;
			int flopOrdinal = flop.ordinal;
			for (Card card : Card.values()) {
				int cardOrdinal = card.ordinal;
				intersectsCard[flopOrdinal][cardOrdinal] = false;
				if(flopCards[0] == card) intersectsCard[flopOrdinal][cardOrdinal] = true;
				if(flopCards[1] == card) intersectsCard[flopOrdinal][cardOrdinal] = true;
				if(flopCards[2] == card) intersectsCard[flopOrdinal][cardOrdinal] = true;
			}
		}
	}
	 
	public boolean pairs;
	public boolean trips;
	public boolean suit1;
	public boolean suit2;
	public boolean suit3;

	private Flop setIsoFlops(Card c1, Card c2, Card c3) {
		Card isoFlopC1 = null, isoFlopC2 = null, isoFlopC3 = null;

		this.suit1 = false; this.suit2 = false; this.suit3 = false;
		this.pairs = false; this.trips = false;
		
		BitSet rankBitSet = new BitSet(13);
		BitSet suitBitSet = new BitSet(4);
		
		rankBitSet.set(c1.rank.ordinal());
		rankBitSet.set(c2.rank.ordinal());
		rankBitSet.set(c3.rank.ordinal());
		suitBitSet.set(c1.suit.ordinal());
		suitBitSet.set(c2.suit.ordinal());
		suitBitSet.set(c3.suit.ordinal());
		
		if (rankBitSet.cardinality() == 1) {
			this.trips = true;
		} else if (rankBitSet.cardinality() == 2) {
			this.pairs = true;
		}
		
		if (suitBitSet.cardinality() == 1) {
			this.suit1 = true;
		} else if (suitBitSet.cardinality() == 2) {
			this.suit2 = true;
		} else if (suitBitSet.cardinality() == 3) {
			this.suit3 = true;
		}
		
		this.suitTransArr[Suit.Clubs.ordinal()] = Suit.Clubs;
		this.suitTransArr[Suit.Diamonds.ordinal()] = Suit.Diamonds;
		this.suitTransArr[Suit.Hearts.ordinal()] = Suit.Hearts;
		this.suitTransArr[Suit.Spades.ordinal()] = Suit.Spades;
		
		
		if (this.suit1) {
			isoFlopC1 = Card.get(c1.rank, Suit.Clubs);
			isoFlopC2 = Card.get(c2.rank, Suit.Clubs);
			isoFlopC3 = Card.get(c3.rank, Suit.Clubs);
			this.suitTransArr[Suit.Clubs.ordinal()] = c1.suit;
			this.suitTransArr[c1.suit.ordinal()] = Suit.Clubs;
		} else if (this.suit2) {
			if (c1.suit.ordinal()==c2.suit.ordinal()) {
				isoFlopC1 = Card.get(c1.rank, Suit.Clubs);
				isoFlopC2 = Card.get(c2.rank, Suit.Clubs);
				isoFlopC3 = Card.get(c3.rank, Suit.Diamonds);
				this.suitTransArr[Suit.Clubs.ordinal()] = c1.suit;
				this.suitTransArr[c1.suit.ordinal()] = Suit.Clubs;
				this.suitTransArr[Suit.Diamonds.ordinal()] = c3.suit;
				this.suitTransArr[c3.suit.ordinal()] = Suit.Diamonds;
			} else if (c1.suit.ordinal()==c3.suit.ordinal()) {
				isoFlopC1 = Card.get(c1.rank, Suit.Clubs);
				isoFlopC2 = Card.get(c2.rank, Suit.Diamonds);
				isoFlopC3 = Card.get(c3.rank, Suit.Clubs);
				this.suitTransArr[Suit.Clubs.ordinal()] = c1.suit;
				this.suitTransArr[c1.suit.ordinal()] = Suit.Clubs;
				this.suitTransArr[Suit.Diamonds.ordinal()] = c2.suit;
				this.suitTransArr[c2.suit.ordinal()] = Suit.Diamonds;
			} else if (c2.suit.ordinal()==c3.suit.ordinal()) {
				if (c1.rank.ordinal() == c2.rank.ordinal()) {
					isoFlopC1 = Card.get(c1.rank, Suit.Clubs);
					isoFlopC2 = Card.get(c2.rank, Suit.Diamonds);
					isoFlopC3 = Card.get(c3.rank, Suit.Clubs);
					this.suitTransArr[Suit.Clubs.ordinal()] = c1.suit;
					this.suitTransArr[c1.suit.ordinal()] = Suit.Clubs;
					this.suitTransArr[Suit.Diamonds.ordinal()] = c2.suit;
					this.suitTransArr[c2.suit.ordinal()] = Suit.Diamonds;
				} else {
					isoFlopC1 = Card.get(c1.rank, Suit.Clubs);
					isoFlopC2 = Card.get(c2.rank, Suit.Diamonds);
					isoFlopC3 = Card.get(c3.rank, Suit.Diamonds);
					this.suitTransArr[Suit.Clubs.ordinal()] = c1.suit;
					this.suitTransArr[c1.suit.ordinal()] = Suit.Clubs;
					this.suitTransArr[Suit.Diamonds.ordinal()] = c2.suit;
					this.suitTransArr[c2.suit.ordinal()] = Suit.Diamonds;
				}
			}
		} else if (this.suit3) {
			isoFlopC1 = Card.get(c1.rank, Suit.Clubs);
			isoFlopC2 = Card.get(c2.rank, Suit.Diamonds);
			isoFlopC3 = Card.get(c3.rank, Suit.Hearts);
			this.suitTransArr[Suit.Clubs.ordinal()] = c1.suit;
			this.suitTransArr[c1.suit.ordinal()] = Suit.Clubs;
			this.suitTransArr[Suit.Diamonds.ordinal()] = c2.suit;
			this.suitTransArr[c2.suit.ordinal()] = Suit.Diamonds;
			this.suitTransArr[Suit.Hearts.ordinal()] = c3.suit;
			this.suitTransArr[c3.suit.ordinal()] = Suit.Hearts;
		}
		
		return Flop.get(isoFlopC1, isoFlopC2, isoFlopC3); 
	}
	
//	private Flop setS0Cards(Card c1, Card c2) {
//		Card s01 = null, s02 = null;
//		s01 = Card.get(c1.rank, Suit.Clubs);		
//
//		if (c1.rank == c2.rank) {
//			s02 = Card.get(c2.rank, Suit.Diamonds);
//		} else {
//			s02 = Card.get(c2.rank, Suit.Clubs);
//		}
//		
//		return Flop.get(s01, s02);
//	}
	private Flop(Card c1, Card c2, Card c3, int ordinal) {
		this.cards[0] = c1;
		this.cards[1] = c2;
		this.cards[2] = c3;
		Flop flop = null;
		this.ordinal = ordinal;
		this.suitTransArr = new Suit[4];
		
		//get preflop ord
		flop = setIsoFlops(c1, c2, c3);
		if (null != flop) {
			this.isoIdx = flop.isoIdx;
			this.isoOrdinal = flop.isoOrdinal;
		} else {
			this.isoIdx = isoOrdCount++;
			this.isoOrdinal = ordinal;
		}
//		
//		//get no suit ord
//		p = setS0Cards(c1,c2);
//		if (null != p) {
//			this.isoOrds0 = p.isoOrds0;
//		} else {
//			this.isoOrds0 = ic0;
//			Flop.s0values[ic0++] = this;
//		}
	}

	static {
		findValues();
		findPairIntersections();
		findCardIntersections();
//		populatePreToPostFlopOrds();
	}
	
//	private static void populatePreToPostFlopOrds() {
//		int preToPostCounters[] = new int[169];
//		int pfCount = 0;
//		for (Flop p:values) {
//			if (preToPostCounters[p.isoOrd] == 0) {
//				isoValues[pfCount++] = p;
//				if (p.suited) {
//					preToPostOrdinals[p.isoOrd] = new int[4];
//				} else if (p.paired) {
//					preToPostOrdinals[p.isoOrd] = new int[6];
//				} else {
//					preToPostOrdinals[p.isoOrd] = new int[12];
//				}
//			}
//			preToPostOrdinals[p.isoOrd][(preToPostCounters[p.isoOrd])++] = p.ordinal;
//		}
//	} 

	private static void findValues() {
		Card[] cards = Card.values();
		int l = 0;
		for (int i = 0; i < Card.count; i++) {
			for (int j = i + 1; j < Card.count; j++) {
				for (int k = j + 1; k < Card.count; k++) {
					Flop flop = new Flop(cards[i], cards[j], cards[k], l);
					values[l] = flop;
					valuesByCard[i][j][k] = flop;
					valuesByCard[i][k][j] = flop;
					valuesByCard[k][i][j] = flop;
					valuesByCard[k][j][i] = flop;
					valuesByCard[j][i][k] = flop;
					valuesByCard[j][k][i] = flop;
					l++;
				}
			}
		}
	}

	public static Flop[] values() {
		return values;
	}
	
//	public static Flop parse(String s) {
//		Card[] cards = new Card[2];
//	
//		for (int i = 0; i < 2; i++) {
//			cards[i] = (Card.parse( s.substring(2*i, 2*i+2)));
//		}
//		
//		Flop result = Flop.get(cards[0], cards[1]);
//		return result;
//	}

	public static boolean[][] getIntersectsCard() {
		return intersectsCard;
	}

	public static boolean[][] getIntersectsPair() {
		return intersectsPair;
	}

	public Flop getIsoFlop() {
		return Flop.values[this.isoOrdinal];
	}

	public Pair transposePair(Pair pair) {
		Card c1 = pair.getCards()[0];
		Card c2 = pair.getCards()[1];
		Card transC1 = Card.get(c1.rank, this.suitTransArr[c1.suit.ordinal()]);
		Card transC2 = Card.get(c2.rank, this.suitTransArr[c2.suit.ordinal()]);
		
		return Pair.get(transC1,transC2);
	}
	
//	public static void main(String[] args) {
//		for (int i = 0; i < Flop.suit0; i++) {
//			System.out.println(Flop.s0values[i].toString());
//		}
//	}
}
