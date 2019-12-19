package util;

public class Sequence {
	private int idx;
	
	public Sequence(int startIdx) {
		super();
		
		idx = startIdx;
	}
	
	public int getNext() {
		int next = idx;
		idx++;
		return next;
	}
}
