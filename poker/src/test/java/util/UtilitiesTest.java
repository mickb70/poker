package util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilitiesTest extends TestCase {
//	UTG+2 : Raises $0.75 to $0.75,15
//	UTG : Raises $0.77 to $0.77,13
//	UTG+2 : Raises $0.75 to $0.75,15
//	Dealer : Raises $2.35 to $2.35,16
//	Dealer  [ME] : Raises $0.60 to $0.60,22
//	UTG+1 : Raises $0.75 to $0.75,15
//	Small Blind  [ME] : Raises $0.65 to $0.75,27
//	Dealer : Raises $0.50 to $0.50,16
//	Small Blind : Raises $0.90 to $1,21
	
	//Small Blind : Bets $0.90 
//	public void testGetOneValue() {
//		String str = "Small Blind : Bets $0.90";
//		
//		Assert.assertEquals(90, Utilities.parseCurrencyFromString(str));
//	}
	
	public void testGetTwoValues( ) {
		String str = "Small Blind  [ME] : Raises $0.65 to $0.75";
		String[] curr = Utilities.parseCurrenciesFromString(str);
		
		Assert.assertEquals(0.65, Double.parseDouble(curr[1]));
		Assert.assertEquals("0.75", curr[2]);
	}
}
