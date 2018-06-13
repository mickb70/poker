package omaha;

public class OmahaEquityCalculator {
	
	public static double[] flopEquities (Hand[] fourCardHands, Hand board) throws DeckInvalidException {
		double[] equities = new double[fourCardHands.length];
		double[] numerators = new double[fourCardHands.length];
		int[] ranks = new int[fourCardHands.length];
		int[] wins = new int[fourCardHands.length];
		int[] chops = new int[fourCardHands.length];
		Hand[] winBoards = new Hand[1000];
		int counter = 0;
		Hand deck = new Hand();
		int expectedCardCount = 0;
		int numBoards = 0;
		
		OmahaEvaluator.initialize();
		
		// initialise deck
		deck.plus(board); 
		expectedCardCount += board.noCards();
		
		//initialise equities and add hands
		for (int i = 0; i < equities.length; i++) {
			equities[i] = 0;
			numerators[i] = 0;
			wins[i] = 0;
			chops[i] = 0;
			deck.plus(fourCardHands[i]);
			expectedCardCount += fourCardHands[i].noCards();
		}
		
		if (deck.noCards() != expectedCardCount ) {
			throw new DeckInvalidException("Dodgy Deck");
		}
		
		for (int i = 0; i < 52; i++) {
			for (int j = i+1; j < 52; j++) {
				if ((!deck.intersects(Card.get(i)))&&(!deck.intersects(Card.get(j)))) {
					deck.addCard(i); board.addCard(i);
					deck.addCard(j); board.addCard(j);
					int maxRank = 0;
					int prevMax = 0;
					int numMax = 0;
					numBoards++;
						
					for (int k = 0; k < fourCardHands.length; k++) {
						ranks[k] = OmahaEvaluator.getRank(fourCardHands[k], board);
						maxRank = Math.max(prevMax, ranks[k]);
						if (maxRank > prevMax) {
							numMax = 1;
						} else if (ranks[k] == maxRank) {
							numMax++;
						}
						prevMax = maxRank;
					}
					
					for (int k = 0; k < fourCardHands.length; k++) {
						if (ranks[k] == maxRank) {
							if (numMax > 1) {								
								chops[k]++;    
							} else {
								wins[k]++;
								if (k==0) {
									winBoards[counter++] = new Hand().plus(board);
								}
							}
							numerators[k] += ((double)1 / (double)numMax);
						}
					}
					
					deck.removeCard(i); board.removeCard(i);
					deck.removeCard(j); board.removeCard(j);
				}
			}
		}
		
		for (int i = 0; i < equities.length; i++) {
			equities[i] = numerators[i] / (double)numBoards;
		}
		
		return equities;
	}

	public static double[] turnEquities (Hand[] fourCardHands, Hand board) throws DeckInvalidException {
		double[] equities = new double[fourCardHands.length];
		double[] numerators = new double[fourCardHands.length];
		int[] ranks = new int[fourCardHands.length];
		Hand deck = new Hand();
		int expectedCardCount = 0;
		int numBoards = 0;
		
		OmahaEvaluator.initialize();
		
		// initialise deck
		deck.plus(board); 
		expectedCardCount += board.noCards();
		
		//initialise equities and add hands
		for (int i = 0; i < equities.length; i++) {
			equities[i] = 0;
			numerators[i] = 0;
			deck.plus(fourCardHands[i]);
			expectedCardCount += fourCardHands[i].noCards();
		}
		
		if (deck.noCards() != expectedCardCount ) {
			throw new DeckInvalidException("Dodgy Deck");
		}
		
		for (int i = 0; i < 52; i++) {
			if (!deck.intersects(Card.get(i))) {
				deck.addCard(i); board.addCard(i);
				int maxRank = 0;
				int prevMax = 0;
				int numMax = 0;
				numBoards++;
				
				for (int k = 0; k < fourCardHands.length; k++) {
					ranks[k] = OmahaEvaluator.getRank(fourCardHands[k], board);
					maxRank = Math.max(prevMax, ranks[k]);
					if (maxRank > prevMax) {
						numMax = 1;
					} else if (ranks[k] == maxRank) {
						numMax++;
					}
					prevMax = maxRank;
				}
				
				for (int k = 0; k < fourCardHands.length; k++) {
					if (ranks[k] == maxRank) {
						numerators[k] += ((double)1 / (double)numMax);
					}
				}
				
				deck.removeCard(i); board.removeCard(i);
			}
		}
		
		for (int i = 0; i < equities.length; i++) {
			equities[i] = numerators[i] / (double)numBoards;
		}
		
		return equities;
	}
}
