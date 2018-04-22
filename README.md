# Twitter-Sentiment-Analysis
Made by Adriealle Dsouza and Anant Mehra
## 1. PROJECT OBJECTIVE
Sentiment Analysis on twitter streamed data using flume and hadoop

## 2. Project Description

### 2.1  Flume Introduction

Apache Flume is a distributed, reliable, and available service for efficiently collecting, aggregating, and moving large amounts of log data. It has a simple and flexible architecture based on streaming data flows. It is robust and fault tolerant with tunable reliability mechanisms and many failover and recovery mechanisms. It uses a simple extensible data model that allows for online analytic application.

### 2.2 FLUME INSTALLATION

* Download Flume
    Download apache-flume-1.7.0-bin.tar.gz
 
 
* Extract using 7-Zip
 Move to C:\flume\apache-flume-1.7.0-bin directory
 
 
* Set Path and Classpath for Flume
    FLUME_HOME=C:\flume\apache-flume-1.7.0-bin
 
    FLUME_CONF=%FLUME_HOME%\conf
 
    CLASSPATH=%FLUME_HOME%\lib\*
 
    PATH=C:\flume\apache-flume-1.7.0-bin\bin
 
 
* Edit log4j.properties file
    flume.root.logger=DEBUG,console
 
    #flume.root.logger=INFO,LOGFILE
 
* Copy flume-env.ps1.template as flume-env.ps1.
    Add below configuration:
 
    $JAVA_OPTS="-Xms500m -Xmx1000m -Dcom.sun.management.jmxremote"
 
 
 
* Twitter keys generation
 
Make a twitter application in twitter developer app and generate the necessary keys which will be used to extract the twitter data using flume.
 
 
* Make a twitter.conf file in conf folder
 
Write the details about the source,sink and channel of twitter in the twitter.conf file along with the twitter keys we have generated.
 
 
### 2.3 WHAT IS SENTIMENT ANALYSIS ?

Sentiment analysis is a process of computationally identifying and categorizing opinions expressed in a piece of text, especially in order to determine whether the writer's attitude towards a particular topic, product, etc. is positive, negative, or neutral.
In this project we are doing the sentiment analysis on twitter data to analyse whether the tweets posted by people are positive or negative or neutral by checking the tweets with the AFFIN dictionary which has a set of 2500 words along with the value of each word ranging  from -5 to +5 denoting whether tweets are positive or negative. 


### 2.4 HOW IS SENTIMENT ANALYSIS WORKING ?

Here are the 4 steps we have followed to perform Sentiment Analysis:
 
 
      1.Implementing Distributed Caching
      2. Writing a mapper class to calculate the sentiments
      3. Writing a reducer class to display all the mapper output
      4. Writing a Driver class for our mapreduce program
 
#### Implementing Distributed Caching
 
In Map Reduce, map-side joins are carried out by distributed cache. Distributed cache is applied when we have two datasets, where the smaller dataset size is limited to the cache memory of the cluster. Here, the dictionary is the smaller dataset, so we are using distributed cache. Here is the implementation of the distributed cache.
 
#### Mapper Class
The map method takes each record as input and the record is converted into a string, using the toStringmethod. After this, we have created a jsonobject called jsonparser, which parses each record which is in JSON format.
 
#### Reducer Class
In the reducer class, we are just passing the input of the mapper as its output.
 
#### Driver Class
In the Driver class, we need to provide the path for the cached dataset, using the below line.We also need to provide the input(tweets_folder) path and the output folder path as arguments.

### 2.5 FLUME WORKING COMMANDS TO PERFORM SENTIMENT ANALYSIS
 
 
 * ##### cd %FLUME_HOME%/bin
 
 
* ##### flume-ng agent –conf %FLUME_CONF% –conf-file %FLUME_CONF%/flume-conf.properties.template –name agent
 
 
* #### flume-ng agent –conf %FLUME_CONF% –conf-file %FLUME_CONF%/flume-conf.properties –name TwitterAgent 
 
 
Fig: Twitter stream command on cmd
 
![Alt Text](https://github.com/adriel1997/Twitter-Sentiment-Analysis/input.PNG)
#### Fig: Scrubbed data retrieved  
 
 

![Alt Text](https://github.com/adriel1997/Twitter-Sentiment-Analysis/twitter stream.PNG)
#### Fig.Data streaming from twitter


![Alt Text](https://github.com/adriel1997/Twitter-Sentiment-Analysis/twitterhdfs.PNG)
#### Fig. Data into HDFS (Log files)


### 3. Technologies Used
In the above project we have used apache Flume – 1.7.0 to stream the data from twitter in the form of log files and then store it in HDFS to apply sentiment analysis on that data.


### 4. Java Code with result snapshots

#### 4.1 Mapper


 ```java
         
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
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
 

public class twitter_mapper extends Mapper<LongWritable, Text, Text, Text> {

	
	private URI[] files;
	 
	private HashMap<String,String> AFINN_map = new HashMap<String,String>();
	 
	@Override
	 
	public void setup(Context context) throws IOException
	 
	{
	 
	
	
		files = DistributedCache.getCacheFiles(context.getConfiguration());
	 
	System.out.println("files:"+ files);
	 
	Path path = new Path(files[0]);
	 
	FileSystem fs = FileSystem.get(context.getConfiguration());
	 
	FSDataInputStream in = fs.open(path);
	 
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	 
	String line="";
	 
	while((line = br.readLine())!=null)
	 
	{
	 
	String splits[] = line.split("\t");
	 
	AFINN_map.put(splits[0], splits[1]);
	 
	}
	 
	br.close();
	 
	in.close();
	 
	}
	
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		String name;
		String twt;
		String line = value.toString();
		String[] tuple = line.split("\\n");
		JSONParser jsonParser = new JSONParser();
		 
		try{
		 
		for(int i=0;i<tuple.length; i++){
		 
		JSONObject obj =(JSONObject) jsonParser.parse(line);
		 
		String tweet_id = (String) obj.get("id_str");
		 
		String tweet_text=(String) obj.get("text");
		twt=(String) obj.get("text");
		String[] splits = twt.toString().split(" ");
		 
		int sentiment_sum=0;
		 
		for(String word:splits){
		 
		if(AFINN_map.containsKey(word))
		 
		{
		 
		Integer x=new Integer(AFINN_map.get(word));
		 
		sentiment_sum+=x;
		 
		}
		 
		}
		 
		context.write(new Text(tweet_id),new Text(tweet_text+"\t----->\t"+new Text(Integer.toString(sentiment_sum))));
		 
		}
		 
		}catch(Exception e){
		 
		e.printStackTrace();
		 
		}
		 
		}

}

```


#### 4.2 Reducer

```java
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class twitter_reducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key,Text value, Context context)
			throws IOException, InterruptedException {
		// process values
		 
		context.write(key,value);
		 
		}
		 
		}

```

#### 4.3 MapReduce Driver
 ```java
         
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
		//ToolRunner.run(new twitter_driver(),args); 
	

//public int run(String[] args) throws Exception {
 
 //TODO Auto-generated method stub

{
Configuration conf =new Configuration();
 

 
//DistributedCache.addCacheFile(new URI("C:/flume/apache-flume-1.7.0-bin/bin/AFINN.txt"),conf);
 
Job job =Job.getInstance(conf,"Sentiment Analysis");
job.addCacheFile(new Path("hdfs://localhost:9000/user/AFINN.txt").toUri());
 
job.setJarByClass(twitter_driver.class);
 
job.setMapperClass(twitter_mapper.class);
 
job.setReducerClass(twitter_reducer.class);
 
job.setMapOutputKeyClass(Text.class);
 
job.setMapOutputValueClass(Text.class);
 
job.setOutputKeyClass(NullWritable.class);
 
job.setOutputValueClass(Text.class);
 
job.setInputFormatClass(TextInputFormat.class);
 
job.setOutputFormatClass(TextOutputFormat.class);
 
FileInputFormat.addInputPath(job, new Path("hdfs://localhost:9000/user/test.txt"));
 
FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/user/o2.txt"));
 
System.exit(job.waitForCompletion(true) ? 0 : 1);
}
}


public Configuration getConf() {
	// TODO Auto-generated method stub
	return null;
}

public void setConf(Configuration arg0) {
	// TODO Auto-generated method stub
	
}



@Override
public int run(String[] arg0) throws Exception {
	// TODO Auto-generated method stub
	return 0;
}
}
```
