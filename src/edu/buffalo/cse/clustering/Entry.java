package edu.buffalo.cse.clustering;


import java.util.ArrayList;

public class Entry {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[][] arr = {
				{-0.69,	-0.96,	-1.16,	-0.66,	-0.55,	0.12,	-1.07,	-1.22,	0.82,	1.4,	0.71,	0.68,	0.11,	-0.04,	0.19,	0.82},
				{-0.21,	0.19,	0.86,	0.04,	-0.35,	-0.39,	-0.51,	-0.2,	0.0,	0.77,	0.41,	0.14,	-0.45,	-1.23,	-0.325,	0.0}
				};
		
		double[][] arr1 = {
				{0.23,	0.11,	1.08,	0.36,	-0.07,	-0.33,	-0.39,	-0.19,	-0.57,	0.83,	0.28,	-0.32,	-0.118,	-0.25,	-0.7,	-0.6},
				{0.09,	0.03,	0.39,	-0.2,	-0.63,	-1.09,	-0.97,	-0.53,	0.11,	0.34,	0.52,	-0.08,	0.11,	0.12,	0.25,	0.59}
				};
		
		ArrayList<double[][]> clusterArray = new ArrayList<double[][]>();
		clusterArray.add(arr);
		clusterArray.add(arr1);
		
		Plot plot = new Plot(clusterArray);
		plot.plot();
		
		
		

	}

}
