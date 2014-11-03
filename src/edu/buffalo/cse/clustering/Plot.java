package edu.buffalo.cse.clustering;


import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jmat.data.AbstractMatrix;

public class Plot {
	
	private ArrayList<double[][]> clusterArray;
	private ArrayList<double[][]> reducedClusterArray;

	public Plot(ArrayList<double[][]> clusterArray){
		this.clusterArray = clusterArray;
		reducedClusterArray = new ArrayList<double[][]>();
	}
	
	
	public void plot(){
		for(double[][] matArray : clusterArray){
			if(matArray.length < 2){
				continue;
			}
			int redDim = 2;
			PCA pca = new PCA(matArray);
			AbstractMatrix resMat = pca.getReducedMatrix(redDim);
			double[][] reducedArray = resMat.getArrayCopy();
			reducedClusterArray.add(reducedArray);
		}
		
		plotGraph();
	}
	
	
	public void plotGraph(){
		EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
            	String title = "Clusters";
                ScatterAdd demo = new ScatterAdd(title, reducedClusterArray);
                demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                demo.pack();
                demo.setLocationRelativeTo(null);
                demo.setVisible(true);
            }
        });
	}

}
