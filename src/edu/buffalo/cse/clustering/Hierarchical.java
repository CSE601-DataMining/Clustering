package edu.buffalo.cse.clustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
			data = Files.readAllLines(Paths.get("data/" + file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Data file not found");
			e.printStackTrace();
		}
		int m = data.size();
		int n = data.get(0).split("\t").length;
		float[][] genes = new float[m][n+1];
		
		for (int i = 0; i < m; i++)
		{
			String[] tuple = data.get(i).split("\t");
			for (int j = 0; j < n; j++)
			{
				genes[i][j] = Float.parseFloat(tuple[j]);
			}
			genes[i][n] = i;
		}
		
		ArrayList<ArrayList<Float>> distMat = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> first_tuple = new ArrayList<Float>(m);
		for (int i = 0; i <= m; i++)
			first_tuple.add((float)i);
		distMat.add(first_tuple);
		for (int i = 0; i < m; i++) {
			ArrayList<Float> tuple = new ArrayList<Float>(m);
			tuple.add((float)i);
			for (int j = 1; j <= m; j++) {
				tuple.add(0f);
			}
			distMat.add(tuple);
		}
		float[][] distanceMatrix = new float[m][m];
		for (int i = 0; i < m; i++)
		{
			for (int j = i+1; j < m-1; j++)
			{
				float distance = 0f;
				if(i != j)
					for (int l = 0; l < n-2; l++)
						distance += Math.pow(genes[i][l+2] - genes[j][l+2], 2);
				distMat.get(i+1).set(j+1, distance);
				distMat.get(j+1).set(i+1, distance);
				distanceMatrix[i][j] = distance;
				distanceMatrix[j][i] = distance;
			}
		}
		
		
		int tempM = m;
		int k = 5;
		System.out.println("Enter number of clusters");
		k = Integer.parseInt(br.readLine());
		while(distMat.size()>k)
		{
			int min_i = 0, min_j = 1, min_i_id = (int)(float)distMat.get(0).get(0), min_j_id = (int)(float)distMat.get(1).get(0);
			float minD = distMat.get(0).get(1);
			
			for (int i = 1; i < tempM; i++) 
				for (int j = i+1; j < tempM+1; j++) 
					if (distMat.get(i).get(j) < minD)
					{
						min_i_id = (int)((float)distMat.get(i).get(0));
						min_j_id = (int)((float)distMat.get(j).get(0));
						min_i = i;
						min_j = j;
						minD = distMat.get(i).get(j);
					}
			for (int i = 0; i < m; i++) 
				if (genes[i][n] == (float)min_j_id)
					genes[i][n] = min_i_id;
			
			
			for (int i = 1; i <= tempM; i++) {
				float min = distMat.get(min_i).get(i) < distMat.get(min_j).get(i)? distMat.get(min_i).get(i): distMat.get(min_j).get(i); 
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
		for (int i = 0; i < m - 1; i++)
			for (int j = i + 1; j < m; j++)
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
		System.out.println((float)num/(num + deno));
		
		//Correlation
		float d =0f, c = 0f;
		for (int i = 0; i < m; i++)
			for (int j = 0; j < m; j++) {
				d += distanceMatrix[i][j];
				c += clustering[i][j];
			}
		
		d = d/(m*m);
		c = c/(m*m);
		float numerator = 0f,d1 = 0f,d2 = 0f;
		for (int i = 0; i < m; i++)
			for (int j = 0; j < m; j++) 
			{
				numerator += (distanceMatrix[i][j] - d) * (clustering[i][j] - c);
				d1 += (distanceMatrix[i][j] - d) * (distanceMatrix[i][j] - d);
				d2 += (clustering[i][j] - c) * (clustering[i][j] - c);
			}
		
		float correlation = numerator/((float)Math.sqrt(d1) * ((float)Math.sqrt(d2)));
		System.out.println(correlation);
		
		ArrayList<ArrayList<double[]>> clusters = new ArrayList<ArrayList<double[]>>(k); 
		ArrayList<Integer> cluster_ids = new ArrayList<Integer>(k);
		for (int i = 0; i < k; i++) {
			float current_id = -1.0f;
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
		
	}

}
