package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class IterationReducer 
extends Reducer<Text,Text,Text,Text> {
    
    public static enum ConvergeCounterIteration{
        CounterIter
    }
    
    private double[] getCentroid(String index) throws IOException{
        Path path = new Path("/kmeansDM/centroid/centroid_" + index + ".txt");
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedReader bf = new BufferedReader(new InputStreamReader(fs.open(path)));
        String centroidsLine = bf.readLine();
        String[] arr = centroidsLine.split("\t");
        double[] centroid = new double[arr.length];
        for(int j = 0; j < arr.length; j++){
            centroid[j] = Double.parseDouble(arr[j].trim());
        }
        bf.close();
        //fs.close();
        
        return centroid;
    }
    
    private double calcDist(double[] centroid, double[] point) throws Exception{
        if(centroid.length != point.length){
            throw new Exception("length of centroid and point doesnt match");
        }
        double sum = 0;
        for(int i = 0; i < centroid.length; i++){
            sum += ((centroid[i] - point[i]) * (centroid[i] - point[i]));
        }
        return Math.sqrt(sum);
    }

    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        String centroidIndex = key.toString();
        double[] centroid = getCentroid(centroidIndex);
        double[] newCentroid = new double[centroid.length];
        for (Text val : values) {
            //context.write(key, val);
            String[] valIn = val.toString().split("#");
            count += Integer.parseInt(valIn[0]);
            String[] valArr = valIn[1].split("\t");
            for(int i = 0; i < valArr.length; i++){
                newCentroid[i] += Double.parseDouble(valArr[i]);
            }
        }
        
        //find avg of all the new centroid points
        for(int i = 0; i < newCentroid.length; i++){
            newCentroid[i] = newCentroid[i]/count;
        }
        
        double distBetnCentroids = 0;
        try {
            distBetnCentroids = calcDist(centroid, newCentroid);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if(distBetnCentroids > 0){
            context.getCounter(ConvergeCounterIteration.CounterIter).increment(1);
        }
        
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < newCentroid.length; i++){
            sb.append(newCentroid[i] + "\t");
        }
        String newCentrString = sb.toString().trim();
        
        Text keyOut = new Text("" + centroidIndex);
        Text valOut = new Text(count + "#" + newCentrString);
        context.write(keyOut, valOut);
        
        Path path = new Path("/kmeansDM/centroid/centroid_" + centroidIndex + ".txt");
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(path, true)));
        bw.write(newCentrString + "\n");
        bw.flush();
        bw.close();
        //fs.close();   
    }
}