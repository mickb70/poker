/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;


/**
 *
 * @author mick
 */
public abstract class Threshold {
	
	public static double getAlpha(double pot, double bet) {
		double s = pot/bet;
		return (s/(1+s));
	}
	
	public static double getXCallThreshold(double pot, double bet) {
		double P = pot/bet;
		return (P/(P+1));
	}
}

