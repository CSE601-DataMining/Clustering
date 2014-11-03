package sample;

import java.io.IOException;

import javax.naming.Context;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sample.IntSumReducer.ConvergeCounter;
import sample.IterationReducer.ConvergeCounterIteration;

public class Driver {

	private static final transient Logger LOG = LoggerFactory.getLogger(Driver.class);

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();		

		LOG.info("HDFS Root Path: {}", conf.get("fs.defaultFS"));
		LOG.info("MR Framework: {}", conf.get("mapreduce.framework.name"));
		/* Set the Input/Output Paths on HDFS */
		String inputPath = "/kmeansDM/input";
		String outputPath = "/kmeansDM/kmeans";

		/* FileOutputFormat wants to create the output directory itself.
		 * If it exists, delete it:
		 */
		deleteFolder(conf,outputPath);

		Job job = Job.getInstance(conf);

		job.setJarByClass(Driver.class);
		job.setMapperClass(TokenizerMapper.class);
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setNumReduceTasks(5);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		if(job.waitForCompletion(true)){
			//job completed successfully
		}
		else{
			System.out.println("word count job not completed successfully");
		}

		long counter = job.getCounters().findCounter(ConvergeCounter.Counter).getValue();
		inputPath = "/kmeansDM/kmeans";
		outputPath = "/kmeansDM/kmeans_iterations";

		/* FileOutputFormat wants to create the output directory itself.
		 * If it exists, delete it:*/
		 int iterCount = 0;
		while(counter > 0){
			iterCount++;
			System.out.println("Number of Iterations: " + iterCount);
			deleteFolder(conf,outputPath);

			job = Job.getInstance(conf);

			job.setJarByClass(Driver.class);
			job.setMapperClass(IterationMapper.class);
			job.setReducerClass(IterationReducer.class);
			job.setNumReduceTasks(5);
			job.setCombinerClass(IterCombiner.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			FileInputFormat.addInputPath(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath));
			if(job.waitForCompletion(true)){
				//job completed successfully
			}
			else{
				System.out.println("Sorting of word count job not completed successfully");
			}
			counter = job.getCounters().findCounter(ConvergeCounterIteration.CounterIter).getValue();
			System.out.println("The counter is: " + counter);
		}
		
		
		//after iteration has stopped
		System.out.println("Number of Iterations: " + iterCount);
        deleteFolder(conf,outputPath);
        
        inputPath = "/kmeansDM/input";
		outputPath = "/kmeansDM/kmeans_iterations";

        job = Job.getInstance(conf);

        job.setJarByClass(Driver.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setNumReduceTasks(5);
        //job.setCombinerClass(IterCombiner.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        if(job.waitForCompletion(true)){
            //job completed successfully
        }
        else{
            System.out.println("Sorting of word count job not completed successfully");
        }
		


	}

	/**
	 * Delete a folder on the HDFS. This is an example of how to interact
	 * with the HDFS using the Java API. You can also interact with it
	 * on the command line, using: hdfs dfs -rm -r /path/to/delete
	 * 
	 * @param conf a Hadoop Configuration object
	 * @param folderPath folder to delete
	 * @throws IOException
	 */
	private static void deleteFolder(Configuration conf, String folderPath ) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(folderPath);
		if(fs.exists(path)) {
			fs.delete(path,true);
		}
	}
}