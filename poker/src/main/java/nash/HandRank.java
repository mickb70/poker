package nash;

import java.util.TreeMap;
import java.util.TreeSet;

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

	public TreeMap<HandRank, TreeSet<PairRank>> getPair() {
		// TODO Auto-generated method stub
		return null;
	}
}
