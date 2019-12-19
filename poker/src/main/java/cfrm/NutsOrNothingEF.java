package cfrm;

public class NutsOrNothingEF implements EquityFunction {
	
	public NutsOrNothingEF() {
		super();
	}

	@Override
	public double getEquity(int gameStateIdx, int heroIdx, int villIdx) {
		if (heroIdx > villIdx) {
			return 1;
		}
		
		return 0;
	}

}
