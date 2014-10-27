package edu.buffalo.cse.clustering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Hierarchical {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
		ArrayList<ArrayList<Float>> genes = new ArrayList<ArrayList<Float>>();
		
		for (int i = 0; i < m; i++)
		{
			genes.add(new ArrayList<Float>());
			String[] tuple = data.get(i).split("\t");
			for (int j = 0; j < n; j++)
			{
				genes.get(i).add(Float.parseFloat(tuple[j]));
			}
		}
		
	}

}
