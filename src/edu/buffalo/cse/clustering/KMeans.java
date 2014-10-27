package edu.buffalo.cse.clustering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;



public class KMeans {
	public static void main(String args[])
	{
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
		}
		
		float[][] means = new float[k][n-2];
		
		for (int i = 0; i < k; i++)
			for (int j = 0; j < n-2; j++)
				means[i][j] = (new Random()).nextFloat()*(9.0f) - 4.5f;
		
		boolean converged = false;
		while (!converged)
		{
			converged = true;
			for (int i = 0; i < m; i++)
			{
				int current_median = (int)genes[i][n];
				float mind = 0f;
				for (int j = 0; j < n-2; j++)
					mind += Math.pow(genes[i][j+2] - means[current_median][j], 2);
				
				for (int h = 0; h < k; h++)
				{
					float current_mind = 0;
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
					float sum = 0f;
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
		int num = 0;
		int deno = 0;
		for (int i = 0; i < m - 1; i++)
			for (int j = i + 1; j < m; j++)
				if (genes[i][n] == genes[j][n] && genes[i][1] == genes[j][1])
					num += 1;
				else if (!(genes[i][n] != genes[j][n] && genes[i][1] != genes[j][1]))
					deno +=1;
		
		System.out.println((float)num/(num + deno));
	}

}
