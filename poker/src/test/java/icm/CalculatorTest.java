package icm;

import junit.framework.TestCase;

public class CalculatorTest extends TestCase {

	public void testCalculateEqs() {
		double[] payouts = {0.5,0.3,0.2};
		double[] stacks = {1450,1450,1450,1450,1250,1050};
		
		Calculator calculator = new Calculator(payouts);
		
		double[] eqs = calculator.calculateEqs(stacks);
		
		assertEquals(.1770, eqs[0], 0.0001);
		assertEquals(.1770, eqs[1], 0.0001);
		assertEquals(.1770, eqs[2], 0.0001);
		assertEquals(.1770, eqs[3], 0.0001);
		assertEquals(.1567, eqs[4], 0.0001);
		assertEquals(.1350, eqs[5], 0.0001);
	}
}
