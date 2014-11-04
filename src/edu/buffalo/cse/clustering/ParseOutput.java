package edu.buffalo.cse.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParseOutput {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws IOException{
		List<String> data = null;
		File kmeansOutput = new File("/home/hduser/KmeansDMOutput");
		ArrayList<double[][]> dataArrList = new ArrayList<double[][]>();
		
		for(File file : kmeansOutput.listFiles()){
			try {
				data = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
				int m = data.size();
				if(m == 0) continue;
				int n = data.get(0).split("\t").length;
				double[][] dataArr = new double[m][n];
				for(int i = 0; i < m; i++){
					String[] tuple = data.get(i).split("\t");
					for(int j = 3; j < n; j++){
						dataArr[i][j] = Double.parseDouble(tuple[j]);
					}
				}
				dataArrList.add(dataArr);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Data file not found");
				e.printStackTrace();
				throw e;
			}
		}
		
		//call pca
		Plot plot = new Plot((ArrayList<double[][]>) dataArrList, "Kmeans of Map Reduce");
		plot.plot();
		
		
	}

}
