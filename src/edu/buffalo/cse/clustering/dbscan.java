package edu.buffalo.cse.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class dbscan {

    static ArrayList<Point> D = new ArrayList<Point>();

    public static void main(String[] args)throws IOException {

        InputStreamReader isr =new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

		System.out.println("Enter the file name in data folder");
        String file = br.readLine();

        System.out.println("Enter Epsilon");
        double epsilon= Double.parseDouble(br.readLine());

        System.out.println("Enter Minpints");

        int minpoints= Integer.parseInt(br.readLine());

        List<String> data = new ArrayList<String>();
        try {
            data = Files.readAllLines(Paths.get("data/"+file),StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Data file not found");
            e.printStackTrace();
        }

        System.out.println("No. of Data Points = "+data.size());

        for (int i=0; i< data.size(); i++)
        {
            Point point = new Point(data.get(i));
            D.add(point);
        }

        System.out.println("Input data processing done");

        performDbscan(D,epsilon, minpoints); 

    }

    public static List<Point> regionQuery(Point P, double epsilon) throws Exception{

        List<Point> neighbor = new ArrayList<Point>();

        for(Point dataset_point : dbscan.D)
        {
            double distance=calcDist(P.dimension, dataset_point.dimension);
            if(distance <= epsilon){
                neighbor.add(dataset_point);
            }
        }
        return neighbor;
    }

    private static double calcDist(double[] centroid, double[] point) throws Exception{
        if(centroid.length != point.length){
            throw new Exception("length of point and the neighbour point doesnt match");
        }
        double sum = 0;
        for(int i = 0; i < centroid.length; i++){
            sum += ((centroid[i] - point[i]) * (centroid[i] - point[i]));
        }
        return Math.sqrt(sum);
    }

    public static void performDbscan(ArrayList<Point> dataPoints,double epsilon, int min_points){

        List<Cluster> clusters= new ArrayList<Cluster>();
        Cluster noise_cluster= new Cluster();

        System.out.println("DAtaPoints= "+dataPoints.size());

        for(Point P : dataPoints)
        {	    
            if(!P.isvisited())
            {
                P.mark_as_visited();
                List<Point> neighbor_pts=new ArrayList<Point>();
                try {
                    neighbor_pts = regionQuery(P,epsilon);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(neighbor_pts.size()<min_points)
                {
                    P.mark_as_noise();
                    noise_cluster.add(P);
                    P.set_cluster_id(noise_cluster.getClusterId());
                }
                else
                {
                    Cluster C = new Cluster(); 
                    clusters.add(C);
                    expandCluster(P,noise_cluster , neighbor_pts, C, epsilon, min_points); 					
                }
            }
        } 	
        System.out.println("Number of clusters formed=" + clusters.size());

		List<double[][]> pca_list = new ArrayList<double[][]>();		
        
		for(Cluster clust :clusters)
        {
            List<Point> list=clust.getClusterPoints();
			
            double[][] matrix = new double[list.size()][list.get(0).dimension.length];
            for(int a=0;a<list.size();a++){
                for(int b=0;b<list.get(0).dimension.length;b++)
                {
                    matrix[a][b]=list.get(a).dimension[b];
                }
            }
            pca_list.add(matrix);
			
            clust.displayCluster();
        }
        findJaccard(dataPoints);

        findCorrelation(dataPoints, clusters);
        System.out.println("*****NOISE CLUSTER*****");
        noise_cluster.displayCluster();

        clusters.add(noise_cluster);

        List<Point> total_points = new ArrayList<Point>();	        
        for(Cluster clust : clusters)
            total_points.addAll(clust.getClusterPoints());
        
        Plot plot = new Plot((ArrayList<double[][]>) pca_list);
		plot.plot();
    }

    public static void findCorrelation(ArrayList<Point> D,List<Cluster> clusters){
        double[][] distanceMatrix = new double[D.size()][D.size()];
        for (int i = 0; i < D.size(); i++)
        {
            for (int j = i+1; j < D.size()-1; j++)
            {
            	double distance = 0d;
                if(i != j)
                    for (int l = 0; l <D.get(0).dimension.length; l++)
                        distance += Math.pow(D.get(i).dimension[l] - D.get(j).dimension[l], 2);
                distanceMatrix[i][j] = Math.sqrt(distance);
                distanceMatrix[j][i] = Math.sqrt(distance);
            }
        }

        
        int clustering[][] = new int[D.size()][D.size()];
        
        for(Cluster clust:clusters)
        {
            for(int i=0;i<clust.getClusterPoints().size();i++)
            {
            	if (clust.getClusterPoints().get(i).isNoise())
            		continue;
                for(int j=0;j<clust.getClusterPoints().size();j++){
                    clustering[clust.getClusterPoints().get(i).get_point_id()-1][clust.getClusterPoints().get(j).get_point_id()-1]=1;
                    clustering[clust.getClusterPoints().get(j).get_point_id()-1][clust.getClusterPoints().get(i).get_point_id()-1]=1;
                }
            }
        }

        int m=D.size();
        float d =0f, c = 0f;

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
        System.out.println(correlation);
    }


    public static void findJaccard(List<Point> total_points){
        double Jaccard=0.0;

        double m11=0;
        double m10=0;
        double m01=0;
        System.out.println("Size = "+total_points.size());
        for(int i=0;i<total_points.size();i++)
        {
            for(int j=i+1;j<total_points.size();j++)
            {  
                if(total_points.get(i).get_cluster_id()==total_points.get(j).get_cluster_id()
                        && total_points.get(i).get_ground_truth()==total_points.get(j).get_ground_truth())
                {
                    m11++;
                }
                else if(total_points.get(i).get_cluster_id()==total_points.get(j).get_cluster_id())
                {
                    m10++;
                }
                else if(total_points.get(i).get_ground_truth()==total_points.get(j).get_ground_truth())
                {
                    m01++;
                }
            }
        }

        Jaccard=m11/(m11+m10+m01);
        System.out.println("Jaccard co-efficient is =" +Jaccard);
    }

    public static void expandCluster(Point P, Cluster noise_cluster, List<Point> neighbor_pts, Cluster C, double epsilon, int min_pts)
    { 
        C.add(P);
        P.mark_as_classified();
        P.set_cluster_id(C.getClusterId());
        int index = 0;

        while(index < neighbor_pts.size())
        {
            Point p_dash = neighbor_pts.get(index);
            if(p_dash.get_point_id()==17)
            {
                System.out.println("$$$$$$$$$$");
            }

            if(!p_dash.isvisited() )
            {
                p_dash.mark_as_visited();
                List<Point> neighbor_pts_dash=new ArrayList<Point>();
                try {
                    neighbor_pts_dash = regionQuery(p_dash,epsilon);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(neighbor_pts_dash.size() >=min_pts){
                    for(Point neighbr_pt_temp : neighbor_pts_dash)
                    {
                        neighbor_pts.add(neighbr_pt_temp);
                    }
                }				
            }
            if( !p_dash.isClassified() || p_dash.isNoise())
            {
                C.add(p_dash);
                if(p_dash.isNoise()){
                    noise_cluster.remove(p_dash);
                    p_dash.setNoiseFalse();
                }
                p_dash.mark_as_classified();
                p_dash.set_cluster_id(C.getClusterId());
            }
            index++;
        }
    }

}

class Cluster{
    private static int i=0;
    private List<Point> cluster_points=new ArrayList<Point>();
    private int cluster_id;
    Cluster()
    {
        cluster_id=++i;
    }

    public void add(Point p){
        cluster_points.add(p);
    }

    public int getClusterId(){
        return cluster_id;
    }

    public void displayCluster(){
        System.out.println("Cluster id = "+getClusterId());
        System.out.println("cluster size = "+size());
        for(Point pt:cluster_points)
        {
            pt.displayPoint();
            System.out.println();
        }
        System.out.println("\n----------------------------------------");
    }

    public List<Point> getClusterPoints(){
        return cluster_points;
    }

    public boolean remove(Point p)
    {
        return cluster_points.remove(p);
    }

    public int size(){
        return cluster_points.size();
    }	
}

class Point{

    private int point_id;
    private int cluster_id;
    private int ground_truth;
    private boolean visited=false;
    public double dimension[];
    private boolean noise=false;
    private boolean classified=false;

    Point(String p){
        String point[]=p.split("\t");
        point_id=Integer.parseInt(point[0]);
        ground_truth=Integer.parseInt(point[1]);

        dimension=new double[point.length-2];
        for(int i=2;i<point.length;i++)
            dimension[i-2]=Double.parseDouble(point[i]);
    }

    public void mark_as_visited(){
        visited=true;
    }

    public void mark_as_classified(){
        classified=true;
    }

    public boolean isClassified()
    {
        return classified;
    }

    public void mark_as_noise(){
        noise=true;
    }

    public boolean isvisited(){
        return visited;
    }

    public boolean isNoise(){
        return noise;
    }

    public void setNoiseFalse(){
        noise=false;
    }


    public int get_point_id()
    {
        return point_id;
    }

    public int get_ground_truth()
    {
        return ground_truth;
    }

    public void displayPoint()
    {
        System.out.print(get_point_id() + ": ");
        for(double dim :dimension)
        {
            System.out.print(dim+",");
        }
    }

    public int get_cluster_id()
    {
        return cluster_id;
    }

    public void set_cluster_id(int id)
    {
        cluster_id=id;
    }

}
