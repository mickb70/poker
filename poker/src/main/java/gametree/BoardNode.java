package gametree;

import java.util.TreeMap;
import java.util.TreeSet;

import nash.HandRank;
import nash.PairRank;
import spears2p2.Hand;
import spears2p2.Pair;
import spears2p2.StateTableEvaluator;

public class BoardNode {
	private final BoardNodeType nodeType;
	private final Hand board;
	private final int nutsRank;
	private final int[] ranks;
	private TreeMap<HandRank, TreeSet<PairRank>> pairRankSets;
	
	private void addPairRank(int rank, PairRank pairRank) {
		HandRank HandRank = new HandRank(rank);
		TreeSet<PairRank> pairRankSet = pairRankSets.get(HandRank);
		
		if (pairRankSet != null) {
			pairRankSet.add(pairRank);
		} else {
			pairRankSet = new TreeSet<PairRank>();
			pairRankSet.add(pairRank);
			this.pairRankSets.put(HandRank, pairRankSet);
		}
	}
	
	public BoardNode(Hand board, BoardNodeType nodeType) {
		StateTableEvaluator.initialize();
		
		this.pairRankSets = new TreeMap<HandRank, TreeSet<PairRank>>();
		
		this.board = new Hand(board.toCards());
		this.nodeType = nodeType;
		this.ranks = new int[1326];

		Hand hand  = new Hand(this.board.toCards());
		
		for (Pair pair: Pair.values()) {			
			if (!hand.intersects(pair)) {
				hand.plus(pair);
				int rank = StateTableEvaluator.getRank(hand);
				PairRank pairRank = new PairRank(pair.ordinal, rank);
				addPairRank(rank, pairRank);
				this.ranks[pair.ordinal] = rank;
				hand.minus(pair);
			}
		}
		
		this.nutsRank = this.pairRankSets.firstKey().getRank();
	}

	public BoardNodeType getNodeType() {
		return nodeType;
	}

	public Hand getBoard() {
		return board;
	}

	public TreeMap<HandRank, TreeSet<PairRank>> getPairRankSets() {
		return pairRankSets;
	}

	public int getNutsRank() {
		return nutsRank;
	}

	public int getRank(Pair pair) {
		return ranks[pair.ordinal];
	}
}
