import java.io.IOException;



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

	public void map(LongWritable ikey, Text ivalue, Context context) throws IOException, InterruptedException {

		
		String name;
		String twt;
		String line = ivalue.toString();
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



