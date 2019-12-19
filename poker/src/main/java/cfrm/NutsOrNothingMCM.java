package cfrm;

import java.util.Arrays;
import java.util.Random;

public class NutsOrNothingMCM implements MonteCarloMachine {
	//Flat Arrays
	private final int flatArrSz;
	private final int[] flatHeroRange;
	private final int[] flatVillRange;
	private char position = 0; // top of deck
	private Random rand = new Random();
	
	public NutsOrNothingMCM() {
		flatArrSz = 2;
		flatHeroRange = new int[flatArrSz];
		flatVillRange = new int[flatArrSz];
		
		flatHeroRange[1] = 1;
		Arrays.fill(flatVillRange, 1);
		
		rand.setSeed(System.currentTimeMillis());
	}

	@Override
	public int getHeroStratSize() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getVillStratSize() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getFlatArraySize() {
		return flatArrSz;
	}
	
	public int randInt(int range) {
		return (int) (rand.nextDouble() * range);
	}
	
	@Override
	public boolean hasNext() {
		if (position >= flatArrSz) {
			return false;
		}
		return true;
	}

	@Override
	public int[] getNextMatch() {
		int[] matchUp = new int[2];
		
		matchUp[0] = flatHeroRange[position];
		matchUp[1] = flatVillRange[position++];
		
		return matchUp;
	}

	@Override
	public int[] extractRandomMatch() {
		if (!hasNext()) {reset();}
		
		int pos = position + randInt(flatArrSz - position);
		int h = flatHeroRange[pos];
		int v = flatVillRange[pos];
		int[] matchUp = new int[2];
		flatHeroRange[pos] = flatHeroRange[position];
		flatHeroRange[position] = h;
		flatVillRange[pos] = flatVillRange[position];
		flatVillRange[position] = v;
		position++;
		
		matchUp[0] = h;
		matchUp[1] = v;
		
		return matchUp;
	}

	@Override
	public void reset() {
		position = 0;
	}

	@Override
	public int[] getHeroFreqs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getVillFreqs() {
		// TODO Auto-generated method stub
		return null;
	}

}
