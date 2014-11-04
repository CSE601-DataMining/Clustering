package edu.buffalo.cse.clustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;




public class KMeans {
	public static void main(String args[]) throws IOException
	{
		List<String> data = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the file name in data folder");
		String file = br.readLine();
		try {
			data = Files.readAllLines(Paths.get("data/" + file), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Data file not found");
			e.printStackTrace();
			throw e;
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
		}
		System.out.println("Should we normalize the data?(y/n)");
		if(br.readLine().toLowerCase().equals("y"))
		{
			for (int i = 2; i < n; i++) {
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
		
		
		double[][] distanceMatrix = new double[m][m];
		for (int i = 0; i < m; i++)
		{
			for (int j = i+1; j < m-1; j++)
			{
				double distance = 0f;
				if(i != j)
					for (int l = 0; l < n-2; l++)
						distance += Math.pow(genes[i][l+2] - genes[j][l+2], 2);
				distanceMatrix[i][j] = Math.sqrt(distance);
				distanceMatrix[j][i] = Math.sqrt(distance);
			}
		}
		int k = 5;
		System.out.println("Enter number of clusters");
		k = Integer.parseInt(br.readLine());
		double[][] means = new double[k][n-2];
		
		for (int i = 0; i < k; i++)
		{
			System.out.println("Enter id of mean " + (i+1));
			int id = Integer.parseInt(br.readLine());
			for (int j = 0; j < n-2; j++)
				means[i][j] = genes[id - 1][j+2];
		}
		
		System.out.println("Enter max number of iterations");
		int iterations = Integer.parseInt(br.readLine());
		boolean converged = false;
		for(int current_iteration = 0; current_iteration < iterations; current_iteration++)
		{
			if (converged)
				break;
			converged = true;
			for (int i = 0; i < m; i++)
			{
				int current_median = (int)genes[i][n];
				double mind = 0f;
				for (int j = 0; j < n-2; j++)
					mind += Math.pow(genes[i][j+2] - means[current_median][j], 2);
				
				for (int h = 0; h < k; h++)
				{
					double current_mind = 0;
					for (int j = 0; j < n-2; j++)
						current_mind += Math.pow(genes[i][j+2] - means[h][j], 2);	
					if (current_mind < mind)
					{
						converged = false;
						mind = current_mind;
						genes[i][n] = h;
					}
				}
			}
			for (int h = 0; h < k; h++)
			{
				for (int j = 0; j < n-2; j++)
				{
					int count = 0;
					double sum = 0f;
					for (int i = 0; i < m; i++)
					{
						if (h == (int)genes[i][n])
						{
							count++;
							sum += genes[i][j+2];
						}
					}
					if (count != 0)
						means[h][j] = sum/count;
				}
			}
		}
	
		
		
		//Jaccard coefficient
	
		//incidence matrix
		int[][] clustering = new int[m][m];
		int[][] groundTruth = new int[m][m];
		int num = 0;
		int deno = 0;
		for (int i = 0; i < m; i++)
			for (int j = 0; j <= i; j++)
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
			double current_id = -1.0f;
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
		 Plot plot = new Plot((ArrayList<double[][]>) pca_list, "KMeans");
		plot.plot();
		
	}

}