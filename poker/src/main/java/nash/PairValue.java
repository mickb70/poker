package nash;

import spears2p2.Pair;

public class PairValue implements Comparable<PairValue> {
	private final Pair pair;
	private final double value;
	private final double newValue;
	private final double oldValue;
	private final double difValue;
	
	

	public PairValue(Pair pair, double value, double newValue, double oldValue, double difValue) {
		super();
		this.pair = pair;
		this.value = value;
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.difValue = difValue;
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
		return pair;
	}
	
	public int getOrdinal() {
		return pair.ordinal;
	}

	public double getValue() {
		return value;
	}

	public double getOldValue() {
		return oldValue;
	}

	public double getNewValue() {
		return newValue;
	}

	public double getDifValue() {
		return difValue;
	}

	public PairValue copy() {
		return new PairValue(this.pair, this.value, this.oldValue, this.newValue, this.difValue);
	}
}
