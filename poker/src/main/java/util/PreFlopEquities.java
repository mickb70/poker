package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.BitSet;
import java.util.TreeSet;

import spears2p2.Pair;


public class PreFlopEquities {
	public static int wins[][] = new int[169][169];
	public static int losses[][]  = new int[169][169];
	public static int chops[][]  = new int[169][169];
	public static double equity[][]  = new double[169][169];
	
	static {
		for (int i = 0; i < 169; i++) {
			try {
				if (!checkFiles(i)) {
					System.out.println("dude where are my files");
					System.exit(42);
				}
			} catch (Exception e) {
				System.out.println("hmm");
				System.out.println(e.toString());
				e.printStackTrace();
				System.exit(42);
			}
		}
		
		for (int i = 0; i < 169; i++) {
			for (int j = 0; j < 169; j++) {
				double num = wins[i][j] + ((double)chops[i][j]/(double)2);
				double denom = (double)(wins[i][j]+chops[i][j]+losses[i][j]);
				equity[i][j] = (double)(double)num /(double)denom;
			}
		}
	}
	
	private static boolean checkFiles(int preFlopOrdinal) throws Exception {
		File f;
		String fileName;
		
		fileName = "resources/pfdat_pfai_only/wins_" + preFlopOrdinal + ".ser";
		f = new File(fileName);
		if (f.exists()) {
			FileInputStream fg = new FileInputStream(fileName);
			ObjectInputStream s = new ObjectInputStream(fg);
			wins[preFlopOrdinal] = (int[]) s.readObject();
		} else {
			System.out.println("No files so do it the hard way");
			throw new Exception("Cannot find "+fileName);
		}
		fileName= "resources/pfdat_pfai_only/losses_" + preFlopOrdinal + ".ser";
		f = new File(fileName);
		if (f.exists()) {
			ObjectInputStream s = new ObjectInputStream(new FileInputStream(fileName));
			losses[preFlopOrdinal] = (int[]) s.readObject();
		}
		fileName= "resources/pfdat_pfai_only/chops_" + preFlopOrdinal + ".ser";
		f = new File(fileName);
		if (f.exists()) {
			ObjectInputStream s = new ObjectInputStream(new FileInputStream(fileName));
			chops[preFlopOrdinal] = (int[]) s.readObject();
		}
		return true;
	}
}