package omaha;


//public class Pair implements Comparable<Pair> {
public class Pair {
	public final static int pfcount = 169;
	public final static int count = (Card.count * (Card.count - 1)) / 2;
	
	private static Pair[] values = new Pair[count];
	public static Pair[] pfvalues = new Pair[169];
	private static Pair[][] valuesByCard = new Pair[Card.count][Card.count];
	private static boolean[][] intersectsPair  = new boolean[count][count];
	private static boolean[][] intersectsCard  = new boolean[count][Card.count];
	public static int[][] preToPostOrdinals = new int[169][];
	


	private static int ica = 0;
	private static int ic0 = 0;
//	private static int ict = 0;
//	private static int ima = 0;
	
	//nosuits
	public final static int suit0 = 91; //c(13,2)+13pairs
	public final static int suit1fd = 338;//c(13,2)*4(fd,hs,ls,ns)+13*2(1s,ns)
	public final static int suit1bd = 169;//c(13,2)*2(fd,nfd)+13(nfd)
	public final static int suit2 = 416;//c(13,2)*5(ffd,fhs,fls,bdfd,ns)+13*2(1s,ns)
	public static Pair[] s0values = new Pair[suit0];
	public static Pair[] s1fdvalues = new Pair[suit1fd];
	public static Pair[] s1bdvalues = new Pair[suit1bd];
	public static Pair[] s2values = new Pair[suit2];
	
	public final int isoOrds0;


	public static Pair get(Card c1, Card c2) {
		return valuesByCard[c1.ordinal][c2.ordinal];
	}
	
	public static int getIndex(int i, int j) {
		return valuesByCard[i][j].ordinal;
	}
	
	public final int ordinal;
	public final int isoOrd;
	
	private final Card[] cards = new Card[2];


	
	public String toString() {
		return cards[0].toString + cards[1].toString;
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
		for (Pair p1 : values) {
			Card[] c1 = p1.cards;
			int i1 = p1.ordinal;
			for (Pair p2 : values) {
				int i2 = p2.ordinal;
				Card[] c2 = p2.cards;
				
				intersectsPair[i1][i2] = false;
				intersectsPair[i2][i1] = false;
				
				if(c1[0].ordinal == c2[0].ordinal) intersectsPair[i1][i2] = intersectsPair[i2][i1] = true;
				if(c1[0].ordinal == c2[1].ordinal) intersectsPair[i1][i2] = intersectsPair[i2][i1] = true;
				if(c1[1].ordinal == c2[0].ordinal) intersectsPair[i1][i2] = intersectsPair[i2][i1] = true;
				if(c1[1].ordinal == c2[1].ordinal) intersectsPair[i1][i2] = intersectsPair[i2][i1] = true;
			}
		}
	}
	
	private static void findCardIntersections() {
		for (Pair p1 : values) {
			Card[] c1 = p1.cards;
			int i1 = p1.ordinal;
			for (Card c2 : Card.values()) {
				int i2 = c2.ordinal;
				intersectsCard[i1][i2] = false;
				if(c1[0] == c2) intersectsCard[i1][i2] = true;
				if(c1[1] == c2) intersectsCard[i1][i2] = true;
			}
		}
	}
	 
	public boolean paired;
	public boolean suited;

	private Pair setPreFlopCards(Card c1, Card c2) {
		Card preFlop1 = null, preFlop2 = null;
		preFlop1 = Card.get(c1.rank, Suit.Clubs);		

		if (c1.suit == c2.suit) {
			preFlop2 = Card.get(c2.rank, Suit.Clubs);
			this.suited = true;
			this.paired = false;
		} else {
			this.suited = false;
			preFlop2 = Card.get(c2.rank, Suit.Diamonds);
			if (c1.rank == c2.rank) {
				this.paired = true;
			} else {
				this.paired = false;
			}
		}
		
		return Pair.get(preFlop1, preFlop2); 
	}
	private Pair setS0Cards(Card c1, Card c2) {
		Card s01 = null, s02 = null;
		s01 = Card.get(c1.rank, Suit.Clubs);		

		if (c1.rank == c2.rank) {
			s02 = Card.get(c2.rank, Suit.Diamonds);
		} else {
			s02 = Card.get(c2.rank, Suit.Clubs);
		}
		
		return Pair.get(s01, s02);
	}
	private Pair(Card c1, Card c2, int ordinal) {
		this.cards[0] = c1;
		this.cards[1] = c2;
		Pair p = null;
		this.ordinal = ordinal;
		
		//get preflop ord
		p = setPreFlopCards(c1, c2);
		if (null != p) {
			this.isoOrd = p.isoOrd;
		} else {
			this.isoOrd = ica++;
		}
		
		//get no suit ord
		p = setS0Cards(c1,c2);
		if (null != p) {
			this.isoOrds0 = p.isoOrds0;
		} else {
			this.isoOrds0 = ic0;
			Pair.s0values[ic0++] = this;
		}
	}

	static {
		findValues();
		findPairIntersections();
		findCardIntersections();
		populatePreToPostFlopOrds();
//		for (int i=0;i<169;i++) {
//			System.out.println();
//			for (int j=0;j<preToPostOrdinals[i].length;j++) {
//				System.out.print(Pair.values[preToPostOrdinals[i][j]].toString()+",");
//			}
//		}
	}
	
	private static void populatePreToPostFlopOrds() {
		int preToPostCounters[] = new int[169];
		int pfCount = 0;
		for (Pair p:values) {
			if (preToPostCounters[p.isoOrd] == 0) {
				pfvalues[pfCount++] = p;
				if (p.suited) {
					preToPostOrdinals[p.isoOrd] = new int[4];
				} else if (p.paired) {
					preToPostOrdinals[p.isoOrd] = new int[6];
				} else {
					preToPostOrdinals[p.isoOrd] = new int[12];
				}
			}
			preToPostOrdinals[p.isoOrd][(preToPostCounters[p.isoOrd])++] = p.ordinal;
		}
	} 

	private static void findValues() {
		Card[] cards = Card.values();
		int k = 0;
		for (int i = 0; i < Card.count; i++) {
			for (int j = i + 1; j < Card.count; j++) {
				Pair pair = new Pair(cards[i], cards[j], k);
				values[k] = pair;
				valuesByCard[i][j] = pair;
				valuesByCard[j][i] = pair;
				k++;
			}
		}
	}

	public static Pair[] values() {
		return values;
	}
	
	public static Pair parse(String s) {
		Card[] cards = new Card[2];
	
		for (int i = 0; i < 2; i++) {
			cards[i] = (Card.parse( s.substring(2*i, 2*i+2)));
		}
		
		Pair result = Pair.get(cards[0], cards[1]);
		return result;
	}

	public static boolean[][] getIntersectsCard() {
		return intersectsCard;
	}

	public static boolean[][] getIntersectsPair() {
		return intersectsPair;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < Pair.suit0; i++) {
			System.out.println(Pair.s0values[i].toString());
		}
	}
}
