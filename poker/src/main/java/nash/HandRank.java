package nash;

public class HandRank implements Comparable<HandRank> {
	private final int rank;
	
	public HandRank(int rank) {
		super();
		this.rank = rank;
	}

	@Override
	public int compareTo(HandRank comp) {
		return (comp.rank - this.rank);
	}

	public int getRank() {
		return this.rank;
	}
}
