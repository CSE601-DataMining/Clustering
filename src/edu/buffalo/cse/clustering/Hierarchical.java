package edu.buffalo.cse.clustering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Hierarchical {

	public static void main(String[] args) {
		List<String> data = null;
		int k = 20;
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
		int tempM = m;
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
		for (int i = 0; i < m; i++)
		{
			System.out.println(genes[i][0] + " " + genes[i][1] + " " + genes[i][n]);
		}
	}

}
