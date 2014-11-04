package edu.buffalo.cse.clustering;


import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jmat.data.AbstractMatrix;

public class Plot {
	
	private ArrayList<double[][]> clusterArray;
	private ArrayList<double[][]> reducedClusterArray;
	private String title;

	public Plot(ArrayList<double[][]> clusterArray, String title){
		this.clusterArray = clusterArray;
		this.title = title;
		reducedClusterArray = new ArrayList<double[][]>();
	}
	
	
	public void plot(){
		int rows = 0;
		int cols = 0;
		//calc number of total rows and columns
		for(double[][] matArray : clusterArray){
//			if(matArray.length < 2){
//				continue;
//			}
//			else{
				rows += matArray.length;
				cols = matArray[0].length;
//			}
		}
		
		//combine all clusters
		double[][] combinedData = new double[rows][cols];
		int m = 0;
		for(double[][] matArray : clusterArray){
			for(int i = 0; i < matArray.length; i++){
				for(int j = 0; j < cols; j++){
					combinedData[m][j] = matArray[i][j]; 
					//System.out.print(combinedData[m][j] + "\t");
				}
				//System.out.println("");
				m++;
			}
		}
		
		//call pca on the combined data
		int redDim = 2;
		PCA pca = new PCA(combinedData);
		AbstractMatrix resMat = pca.getReducedMatrix(redDim);
		double[][] reducedArray = resMat.getArrayCopy();
		
		//seperate the reduced dim data into different clusters
		m = 0;
		for(double[][] matArray : clusterArray){
			int size = matArray.length;
			double[][] reducedCluster = new double[size][cols];
			for(int i = 0; i < size; i++){
				for(int j = 0; j < redDim; j++){
					reducedCluster[i][j] = reducedArray[m][j]; 
				}
				m++;
			}
			reducedClusterArray.add(reducedCluster);
		}
		
		
//		for(double[][] matArray : clusterArray){
//			if(matArray.length < 2){
//				continue;
//			}
//			int redDim = 2;
//			PCA pca = new PCA(matArray);
//			AbstractMatrix resMat = pca.getReducedMatrix(redDim);
//			double[][] reducedArray = resMat.getArrayCopy();
//			reducedClusterArray.add(reducedArray);
//		}
		plotGraph();
	}
	
	
	public void plotGraph(){
		EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
            	//String title = "Clusters";
                ScatterAdd demo = new ScatterAdd(title, reducedClusterArray);
                demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                demo.pack();
                demo.setLocationRelativeTo(null);
                demo.setVisible(true);
            }
        });
	}

}
