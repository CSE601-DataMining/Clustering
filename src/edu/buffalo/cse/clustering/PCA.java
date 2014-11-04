package edu.buffalo.cse.clustering;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmat.data.*;
import org.jmat.data.matrixDecompositions.*;
import org.jmat.gui.*;

//import Jama.Matrix;

/**
 * <p>Description : Principal Component Analysis</p>
 * <p>Copyright : GPL</p>
 * @author Yann RICHET
 */

public class PCA {

	private AbstractMatrix covariance;
	private AbstractMatrix EigenVectors;
	private AbstractMatrix EigenValues;
	private double[][] rawData;
	
	
	public PCA(double[][] rawData){
		this.rawData = rawData;
	}

//	public PCA(AbstractMatrix X) {
//		covariance = X.covariance();
//		EigenvalueDecomposition e = covariance.eig();
//		EigenVectors = e.getV();
//		EigenValues = e.getD();
//	}

	public AbstractMatrix getVectors() {
		return EigenVectors;
	}

	public AbstractMatrix getValues() {
		return EigenValues;
	}

public static void main(String[] arg) {
//		int redDim = 2;
//		//construct the matrix X
////		double[][] m1 = {{1}, {2}};
////		double[][] m2 = {{1,2}};
////		AbstractMatrix m1a = new Matrix(m1);
////		AbstractMatrix m2a = new Matrix(m2);
////		AbstractMatrix res = m2a.times(m1a);
////		
////		AbstractMatrix x1 = RandomMatrix.normal(100, 1, 0, 1);
////		AbstractMatrix x2 = RandomMatrix.normal(100, 1, 0, 1);
////		AbstractMatrix X = x1.plus(x2).mergeColumns(x2);
//		double[][] arr = {
//				{19, 63},
//				{39, 74},
//				{30, 87},
//				{30, 23},
//				{15, 35},
//				{15, 43},
//				{15, 32},
//				{30, 73},
//				};
		
	double[][] arr  = {
				{-5.1, 9.25},
				{14.9, 20.25},
				{5.9, 33.25},
				{5.9, -30.75},
				{-9.1, -18.75},
				{-9.1, -10.75},
				{-9.1, -21.75},
				{5.9, 19.25}
				};
		
	PCA pca = new PCA(arr);
	AbstractMatrix resMat = pca.getReducedMatrix(1);
	double[][] reducedArray = resMat.getArrayCopy();
//		
////		double[][] arr = {
////				{-0.69,	-0.96,	-1.16,	-0.66,	-0.55,	0.12,	-1.07,	-1.22,	0.82,	1.4,	0.71,	0.68,	0.11,	-0.04,	0.19,	0.82},
////				{-0.21,	0.19,	0.86,	0.04,	-0.35,	-0.39,	-0.51,	-0.2,	0.0,	0.77,	0.41,	0.14,	-0.45,	-1.23,	-0.325,	0.0}
////				};
////		
//		
//		AbstractMatrix X1 = new org.jmat.data.Matrix(arr);
//
//		PCA pca = new PCA(X1);
//		
//		//make list of all eigen values
//		AbstractMatrix ev = pca.getValues().max();
//		List<EigenVal> evList = new ArrayList<EigenVal>();
//		for(int i = 0; i < ev.getColumnDimension(); i++){
//			EigenVal eval = new EigenVal(i, ev.get(0, i));
//			evList.add(eval);
//		}
//		
//		Collections.sort(evList);
//		
//		AbstractMatrix finalMat = getReducedMatrix(2, pca.getVectors(), evList, X1);
//
//		//display a Frame with data in a 2D-Plot and EigenValues and EigenVectors in the command line.
//		//new FrameView(finalMat.toPlot2DPanel("[x1+x2,x2]", PlotPanel.SCATTER));
////		Plot2DPanel panel1 = finalMat.toPlot2DPanel("[x1+x2,x2]", PlotPanel.SCATTER);
////		Plot2DPanel panel2 = finalMat.toPlot2DPanel("[x1,x2]", PlotPanel.SCATTER);
////		Plot2DPanel[] panels = {panel1, panel2};
////		FrameView fv = new FrameView(panels);
////		pca.getValues().toCommandLine("EigenValues");
////		pca.getVectors().toCommandLine("EigenVectors");
////		pca.covariance.toCommandLine("covariance");
	}
	
	public AbstractMatrix getReducedMatrix(int redDim){
		AbstractMatrix X1 = new org.jmat.data.Matrix(rawData);
		//PCA pca = new PCA(X1);
		covariance = X1.covariance();
		EigenvalueDecomposition e = covariance.eig();
		EigenVectors = e.getV();
		EigenValues = e.getD();
		
		//make list of all eigen values
		AbstractMatrix ev = this.getValues().max();
		List<EigenVal> evList = new ArrayList<EigenVal>();
		for(int i = 0; i < ev.getColumnDimension(); i++){
			EigenVal eval = new EigenVal(i, ev.get(0, i));
			evList.add(eval);
		}
		
		Collections.sort(evList);
		
		AbstractMatrix finalMat = getReducedMatrix(redDim, this.getVectors(), evList, X1);
		return finalMat;
		
	}
	
	public AbstractMatrix getReducedMatrix(int dim, AbstractMatrix eVectors, List<EigenVal> evList, AbstractMatrix data){
		AbstractMatrix finalMat = null;
		for(int i = 0; i < dim; i++){
			AbstractMatrix eVec = eVectors.getRow(evList.get(i).getIndex());
			double[][] newCol = new double[data.getRowDimension()][1];
			for(int j = 0; j < data.getRowDimension(); j++){
				AbstractMatrix dataRow = data.getRow(j).transpose();
				newCol[j][0] = eVec.times(dataRow).get(0, 0);
			}
			if(finalMat == null){
				finalMat = new Matrix(newCol);
			}
			else{
				AbstractMatrix newAbsCol = new Matrix(newCol);
				finalMat = finalMat.mergeColumns(newAbsCol);
			}
		}
		
		return finalMat;
	}
	
}

class EigenVal implements Comparable<EigenVal>{
	int index;
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	double val;
	
	EigenVal(int index, double val){
		this.index = index;
		this.val = val;
	}

	@Override
	public int compareTo(EigenVal o) {
		double oVal = ((EigenVal) o).getVal(); 
		if(oVal > this.val) return 1;
		else if(oVal < this.val) return -1;
		else return 0;
	}
	
	@Override
	public String toString() {
		return index + ":" + this.val;
	}
}