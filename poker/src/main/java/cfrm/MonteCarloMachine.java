package cfrm;

public interface MonteCarloMachine {

	int getHeroStratSize();

	int getVillStratSize();

	int getFlatArraySize();
	
	boolean hasNext();
	
	int[] getNextMatch();

	int[] extractRandomMatch();

	void reset();

	int[] getHeroFreqs();

	int[] getVillFreqs();
}
