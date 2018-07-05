package util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ThresholdTest extends TestCase {
	
	public void testPotSizedBet() {
		Assert.assertEquals(.5, Threshold.getXCallThreshold(50, 50), .00001);
	}
	
	public void testhalfPotBet() {
		Assert.assertEquals(.6666666, Threshold.getXCallThreshold(50, 25), .00001);
	}
	
	public void test2xPotBet() {
		Assert.assertEquals(.3333333, Threshold.getXCallThreshold(50, 100), .00001);
	}

}
