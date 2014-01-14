package nash;

public class PairRank implements Comparable<PairRank> {
	private final int ordinal;
	private final int rank;
	
	public PairRank(int ordinal, int rank) {
		super();
		this.ordinal = ordinal;
		this.rank = rank;
	}

	@Override
	public int compareTo(PairRank comp) {
		if (this.rank != comp.rank) {
			return (comp.rank - this.rank);
		} else {
			return (comp.ordinal - this.ordinal);
		}
	}

	public int getOrdinal() {
		return this.ordinal;
	}

	public int getRank() {
		return this.rank;
	}
}
