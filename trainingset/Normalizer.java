package trainingset;

import fileIO.CSV;
import utilities.CustomMath;
import utilities.CustomArrays;

/***
 * Normalizes a dataset.
 * @author gokul
 */
public class Normalizer {
	private final static String srcfilename = "/home/gokul/workspace/SSH_ML/temp.csv";
	private final static String dstfilename = "/home/gokul/workspace/SSH_ML/temp1.csv";

	public static void main(String[] args) {
		double[][] data = CSV.readCSVToDoubleArray(srcfilename, ",");
		double[][] norData = Normalizer.doMaxMinScaling(data);
		String[][] strData = CustomArrays.convertToStringArray(norData);
		CSV.writeCSV(strData, dstfilename, ",");
	}

	/**
	 * Normalise a 2D array with the below formula value_new = (value -
	 * min)/(max - min)
	 *
	 * @param data
	 * @return normalised data
	 */
	public static double[][] doMaxMinScaling(double[][] data) {
		double[][] norData = null;

		data = CustomArrays.switchArrayRowColumn(data);

		norData = new double[data.length][];

		for (int i = 0; i < data.length; i++) {
			norData[i] = CustomMath.doMaxMinScaling(data[i]);
			System.out.println("Column " + i + " -> Done");
		}

		norData = CustomArrays.switchArrayRowColumn(norData);

		return norData;
	}
}
