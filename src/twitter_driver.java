import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class twitter_driver implements Tool {

public static void main(String[] args) throws Exception {


	//public static void main(String[] args) throws Exception {
		ToolRunner.run(new twitter_driver(),args); 
	//}

//public int run(String[] args) throws Exception {
 
 //TODO Auto-generated method stub

//{
Configuration conf =new Configuration();
 

 
//DistributedCache.addCacheFile(new URI("C:/Users/Adriel/Downloads/AFINN.txt"),conf);
 
Job job =Job.getInstance(conf,"Sentiment Analysis");

job.addCacheFile(new Path("C:/Users/Adriel/Downloads/AFINN.txt").toUri());
 
job.setJarByClass(twitter_driver.class);
 
job.setMapperClass(twitter_mapper.class);
 
job.setReducerClass(twitter_reducer.class);
 
job.setMapOutputKeyClass(Text.class);
 
job.setMapOutputValueClass(Text.class);
 
job.setOutputKeyClass(NullWritable.class);
 
job.setOutputValueClass(Text.class);
 
job.setInputFormatClass(TextInputFormat.class);
 
job.setOutputFormatClass(TextOutputFormat.class);
 
FileInputFormat.addInputPath(job, new Path(args[0]));
 
FileOutputFormat.setOutputPath(job, new Path(args[1]));
 
System.exit(job.waitForCompletion(true) ? 0 : 1);
 
 //return 0;
}

//}

@Override
public Configuration getConf() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void setConf(Configuration arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public int run(String[] arg0) throws Exception {
	// TODO Auto-generated method stub
	return 0;
}
}