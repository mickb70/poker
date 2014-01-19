package nash;

import spears2p2.Pair;

public class PairValue implements Comparable<PairValue> {
	private final Pair pair;
	private final double value;
	
	public PairValue(Pair pair, double value) {
		super();
		this.pair = pair;
		this.value = value;
	}

	@Override
	public int compareTo(PairValue comp) {
		if (this.value > comp.value) {
			return -1;
		} else if (comp.value > this.value) {
			return 1;
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

	public double getValue() {
		return this.value;
	}
}
