package edu.buffalo.cse.clustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Hierarchical {

	public static void main(String[] args) throws IOException {
		List<String> data = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the file name in data folder");
		String file = br.readLine();
		
		try {
			data = Files.readAllLines(Paths.get("data/" + file),StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Data file not found");
			e.printStackTrace();
		}
		int m = data.size();
		int n = data.get(0).split("\t").length;
		double[][] genes = new double[m][n+1];
		
		for (int i = 0; i < m; i++)
		{
			String[] tuple = data.get(i).split("\t");
			for (int j = 0; j < n; j++)
			{
				genes[i][j] = Double.parseDouble(tuple[j]);
			}
			genes[i][n] = i;
		}
		System.out.println("Should we normalize the data?(y/n)");
		if(br.readLine().toLowerCase().equals("y"))
		{
			for (int i = 0; i < n-2; i++) {
				double min = 0d;
				double max = 0d;
				for (int j = 0; j < m; j++) {
					if(genes[j][i] > max)
						max = genes[j][i];
					if(genes[j][i] < min)
						min = genes[j][i];
				}
				for (int j = 0; j < m; j++) {
					genes[j][i] = (genes[j][i] - min)/(max-min);
				}
			}
		}
		
		ArrayList<ArrayList<Double>> distMat = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> first_tuple = new ArrayList<Double>(m);
		for (int i = 0; i <= m; i++)
			first_tuple.add((double)i);
		distMat.add(first_tuple);
		for (int i = 0; i < m; i++) {
			ArrayList<Double> tuple = new ArrayList<Double>(m);
			tuple.add((double)i+1);
			for (int j = 1; j <= m; j++) {
				tuple.add(0d);
			}
			distMat.add(tuple);
		}
		double[][] distanceMatrix = new double[m][m];
		for (int i = 0; i < m; i++)
		{
			for (int j = i+1; j < m; j++)
			{
				double distance = 0d;
				if(i != j)
					for (int l = 0; l < n-2; l++)
						distance += Math.pow(genes[i][l+2] - genes[j][l+2], 2);
				distMat.get(i+1).set(j+1, distance);
				distMat.get(j+1).set(i+1, distance);
				distanceMatrix[i][j] = Math.sqrt(distance);
				distanceMatrix[j][i] = Math.sqrt(distance);
			}
		}
		
		
		int tempM = m;
		int k = 5;
		System.out.println("Enter number of clusters");
		k = Integer.parseInt(br.readLine());
		while(distMat.size()>k +1)
		{
			
			int min_i = 0, min_j = 1, min_i_id = (int)(double)distMat.get(0).get(0), min_j_id = (int)(double)distMat.get(1).get(0);
			double minD = 100d;
			
			for (int i = 1; i < tempM; i++) 
				for (int j = i+1; j < tempM+1; j++) 
					if (distMat.get(j).get(i) < minD)
					{
						min_i_id = (int)((double)distMat.get(i).get(0))-1;
						min_j_id = (int)((double)distMat.get(j).get(0))-1;
						min_i = i;
						min_j = j;
						minD = distMat.get(i).get(j);
					
					}
			
			for (int i = 0; i < m; i++) 
				if (genes[i][n] == (double)min_j_id)
					genes[i][n] = min_i_id;
			
			
			for (int i = 1; i <= tempM; i++) {
				double min = distMat.get(min_i).get(i) < distMat.get(min_j).get(i)? distMat.get(min_i).get(i): distMat.get(min_j).get(i); 
				distMat.get(min_i).set(i, min);
				distMat.get(i).set(min_i, min);
			}
			
			
			for (int i = 0; i <= tempM; i++)
				distMat.get(i).remove(min_j);
			distMat.remove(min_j);
			tempM--;
		}
		
		//Jaccard coefficient
		
			//incidence matrix
			int[][] clustering = new int[m][m];
			int[][] groundTruth = new int[m][m];
			int num = 0;
			int deno = 0;
			for (int i = 0; i < m; i++)
				for (int j = 0; j < m; j++)
				{
					if (genes[i][n] == genes[j][n] && genes[i][1] == genes[j][1] && genes[i][1]!= -1)
						num += 1;
					else if (!(genes[i][n] != genes[j][n] && genes[i][1] != genes[j][1]))
						deno +=1;
					if (genes[i][n] == genes[j][n])
					{
						clustering[i][j] = 1;
						clustering[j][i] = 1;
					}
					if (genes[i][1] == genes[j][1] && genes[i][1]!= -1)
					{
						groundTruth[i][j] = 1;
						groundTruth[j][i] = 1;
					}
				}
			System.out.println("Jaccard coefficient: " +(double)num/(num + deno));
			
			//Correlation
			double d =0f, c = 0f;
			for (int i = 0; i < m; i++)
				for (int j = 0; j < m; j++) {
					d += distanceMatrix[i][j];
					c += clustering[i][j];
				}
			
			d = d/(m*m);
			c = c/(m*m);
			double numerator = 0f,d1 = 0f,d2 = 0f;
			for (int i = 0; i < m; i++)
				for (int j = 0; j < m; j++) 
				{
					numerator += (distanceMatrix[i][j] - d) * (clustering[i][j] - c);
					d1 += (distanceMatrix[i][j] - d) * (distanceMatrix[i][j] - d);
					d2 += (clustering[i][j] - c) * (clustering[i][j] - c);
				}
			
			double correlation = numerator/(Math.sqrt(d1) * (Math.sqrt(d2)));
			System.out.println("Correlation: " +correlation);
			
		ArrayList<ArrayList<double[]>> clusters = new ArrayList<ArrayList<double[]>>(k); 
		ArrayList<Integer> cluster_ids = new ArrayList<Integer>(k);
		for (int i = 0; i < k; i++) {
			double current_id = -1.0d;
			for (int j = 0; j < m; j++) {
				if ((!cluster_ids.contains((int)genes[j][n])) && current_id == -1.0f)
				{
					current_id = genes[j][n];
					cluster_ids.add((int)current_id);
					
					clusters.add(new ArrayList<double[]>());
					double[] point = new double[n-2];
					for (int h = 0; h < n-2; h++)
						point[h] = genes[j][h+2];
					clusters.get(i).add(point);
				}
				else if( genes[j][n] == current_id)
				{
					double[] point = new double[n-2];
					for (int h = 0; h < n-2; h++)
						point[h] = genes[j][h+2];
					clusters.get(i).add(point);
				}
			}
		}
		
		List<double[][]> pca_list = new ArrayList<double[][]>();
		   
		for(ArrayList<double[]> cluster:clusters){
		    double[][] array= new double[cluster.size()][n-2];
		    pca_list.add(cluster.toArray(array));
		}

		//call pca
		 Plot plot = new Plot((ArrayList<double[][]>) pca_list);
			plot.plot();

	}

}
