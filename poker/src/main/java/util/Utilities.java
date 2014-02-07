package util;

public abstract class Utilities {
	
	public static double getDoubleArrayTotal(double[] arr) {
		double total = 0;
		
		for (int i = 0; i < arr.length; i++) {
			total += arr[i];
		}
		
		return total;		
	}
}
