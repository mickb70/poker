package util;

public abstract class Utilities {
	
	public static double getDoubleArrayTotal(double[] arr) {
		double total = 0;
		
		for (int i = 0; i < arr.length; i++) {
			total += arr[i];
		}
		
		return total;		
	}

	public static String[] parseCurrenciesFromString(String str) {
		String[] currs = str.split("\\$");
		String[] strippedCurr = new String[currs.length];
		
		
		for (int i = 0; i < currs.length; i++) {
			strippedCurr[i] = currs[i].replaceAll("[\\sa-zA-Z]", "");
		}
		
		return strippedCurr;
	}
}
