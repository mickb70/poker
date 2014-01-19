package nash;

import spears2p2.Pair;

public class PairRank implements Comparable<PairRank> {
	private final Pair pair;
	private final int rank;
	
	public PairRank(Pair pair, int rank) {
		super();
		this.pair = pair;
		this.rank = rank;
	}

	@Override
	public int compareTo(PairRank comp) {
		if (this.rank != comp.rank) {
			return (comp.rank - this.rank);
		} else {
			return (comp.pair.ordinal - this.pair.ordinal);
		}
	}
	
	public Pair getPair() {
		return this.pair;
	}

	public int getOrdinal() {
		return this.pair.ordinal;
	}

	public int getRank() {
		return this.rank;
	}
}
