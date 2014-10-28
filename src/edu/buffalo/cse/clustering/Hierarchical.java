package edu.buffalo.cse.clustering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Hierarchical {

	public static void main(String[] args) {
		List<String> data = null;
		int k = 5;
		try {
			data = Files.readAllLines(Paths.get("data/cho.txt"));
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
		for (int i = 0; i < m; i++) {
			ArrayList<Float> tuple = new ArrayList<Float>(m);
			for (int j = 0; j < m; j++) {
				tuple.add(0f);
			}
			distMat.add(tuple);
		}
		
		for (int i = 0; i < m-1; i++)
		{
			for (int j = i+1; j < m; j++)
			{
				float distance = 0f;
				if(i != j)
					for (int l = 0; l < n-2; l++)
						distance += Math.pow(genes[i][l+2] - genes[j][l+2], 2);
				distMat.get(i).set(j, distance);
				distMat.get(j).set(i, distance);
			}
		}
		
		/*for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < m; j++)
			{
				System.out.print(distMat.get(i).get(j) + "\t");
			}
			System.out.println();
		}*/
		
		while(distMat.size()>k)
		{
			int min_i = 0, min_j = 1;
			float minD = distMat.get(0).get(1);
			
			for (int i = 0; i < m-1; i++) 
				for (int j = i+1; j < m; j++) 
					if (distMat.get(i).get(j) < minD)
					{
						min_i = i;
						min_j = j;
						minD = distMat.get(i).get(j);
					}
			for (int i = 0; i < m; i++) 
				if (genes[i][n] == (float)min_j)
					genes[i][n] = min_i;
			
			
			for (int i = 0; i < m; i++) {
				
			}
		}
	}

}
