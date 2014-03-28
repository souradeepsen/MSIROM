/* 



THIS PROGRAM COUNTS THE NUMBER OF TIMES EACH PAIR OF WORDS IN A GIVEN SET OF DOCUMENTS APPPEAR USING THE " STRIPES " APPROACH.

THE MAP METHOD TAKES THE DOCUMENT ID AND DOCUMENT TEXT AS INPUT IN THE FORM OF KEY/VALUE PAIRS AND EMITS INTERMEDIATE KEY/VALUE PAIRS WHEREIN, KEY= WORD VALUE=ASSOCIATIVE ARRAY THAT ENCODES THE CO-OCCURENCE COUNTS OF THE NEIGHBOURS OF THE SAME WORD AS THE KEY

THE EXECUTION FRAMEWORK ENSURES THAT ALL ASSOCIATATIVE ARRAYS FOR THE SAME KEY WILL BE BROUGHT TOGETHER IN THE REDUCE PHASE OF PROCESSING

THE REDCUCER PERFORMS AN ELEMENT-WISE SUM OF ALL THE ASSOCIATIVE ARRAYS WITH THE SAME KEY, ACCUMULATING COUNTS THAT CORRESPOND TO THE SAME WORD AS THE KEY

FINALLY, EACH REDUCE METHOD IN THE REDUCER EMITS KEY/VALUE PAIRS WHERE:
KEY=THE CO-OCURRING PAIR OF WORDS
VALUE= COUNT OF CO-OCCURENCES IN THE SET OF DOCUMENTS 


*/










package org.apache.hadoop.examples;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.util.Map;
import java.util.HashMap;

public class JointOccurrence {

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, MapWritable> {					//THE MAPPER CLASS

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {				// MAP METHOD. ITS CALLED FOR EACH KEY/VALUE PAIR IN THE INPUT SPLIT 
			StringTokenizer itr = new StringTokenizer(value.toString());			// USED TO ITERATE THROUGH THE INPUT KEY/VALUE PAIRS
			// System.out.println("mapper  " + value.toString() );
			String prev = null;
			String current = null;
			MapWritable resultMap = new MapWritable();					// USED TO INTERMEDIATE KEY/VALUE PAIRS
			while (itr.hasMoreTokens()) {
				current = itr.nextToken();						// RETRIEVING THE NEXT WORD
				if (prev != null) {							
					// System.out.println("mapper processing " + current );
					MapWritable map = (MapWritable) resultMap			// RETREIVE THE ASSOCIATIVE ARRAY OF THE NEIGHBORING WORD	
							.get(new Text(prev));
					if (map == null) { 						// IF ASSOCIATIVE ARRAY == NULL
						map = new MapWritable(); 
						Text tmpWord = new Text(prev);
						resultMap.put(tmpWord, map);				// MAKING A KEY/VALUE ENTRY TO THE RESULT 
					}
					if (!map.containsKey(new Text(current))) {  			// IF ASSOCIATIVE ARRAY DOESNT CONTAIN AN ENTRY WITH KEY = CURRENT WORD			
						map.put(new Text(current), new IntWritable(0));         // MAKING A KEY/VALUE ENTRY TO THE RESULT. KEY=CURRENT WORD VALUE= 0
					}
					int count = ((IntWritable) map.get(new Text(current)))		// RETRIEVING THE PRESENT COUNT OF THE CURRENT WORD
							.get();
					// System.out.println("mapper putting " + new Text(current)
					// + "," + new IntWritable(count + 1 ));
					map.put(new Text(current), new IntWritable(count + 1));		// MAKING A KEY/VALUE ENTRY TO THE RESULT. KEY= CURRENT WORD VALUE= PRESENT COUNT + 1
				}
				prev = current;								// MAKING THE CURRENT WORD OF THIS ITERATION INTO THE PREVIOUS WORD FOR THE NEXT ITERATION							
			}	
			for (java.util.Map.Entry<Writable, Writable> tmp : resultMap			// ITERATING THROUGH THE RESULT
					.entrySet()) {
				// System.out.println("mapper emiting " + tmp.getKey() + ", " +
				// tmp.getValue());
				context.write((Text) tmp.getKey(), (MapWritable) tmp.getValue());	// EMITTING THE INTERMEDIATE KEY/VALUE PAIRS. KEY=WORD VALUE=ASSOCIATIVE ARRAY HAT ENCODES THE CO-OCCURENCE COUNTS OF THE NEIGHBOURS OF THE SAME WORD AS THE KEY

			}
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, MapWritable, Text, IntWritable> {					// THE REDUCER CLASS

		public void reduce(Text key, Iterable<MapWritable> values,
				Context context) throws IOException, InterruptedException {		// REDUCE METHOD. IT IS CALLED FOR EACH INTERMEDIATE KEY/VALUE PAIR OF THE INPUT ASSIGNED TO THE REDUCER
			// System.out.println("reduce: " + key.toString());		
			MapWritable result = new MapWritable();						
			for (MapWritable val : values) {						// ITERATING THROUGH THE STRIPES
				// System.out.println("in outer iteration");
				for (java.util.Map.Entry<Writable, Writable> tmp : val			// ITERATING THROUGH THE ELEMENTS IN EACH STRIPE
						.entrySet()) {
					// System.out.println("in inner iteration");
					IntWritable count = null;
					if (((count = (IntWritable) result.get(tmp.getKey())) == null)) { 
						result.put(tmp.getKey(), tmp.getValue());
					} else {
						IntWritable i = (IntWritable) tmp.getValue();
						result.put(tmp.getKey(), new IntWritable(i.get()	// MAKING A KEY/VALUE ENTRY INTO THE RESULT WITH UPDATED VALUE OF COUNT
								+ count.get()));
					}
				}
			}
			// System.out.println("writing " + key + " " + result);
			for (java.util.Map.Entry<Writable, Writable> cotextToCount : result		// ITERATING THROUGH THE RESULT
					.entrySet()) {
				String keyValPair = key + "," + (Text) cotextToCount.getKey();		// COMBINING EACH WORD WITH ITS NEIGHBOURNG WORDS TO CREATE PAIRS
				context.write(new Text(keyValPair),
						(IntWritable) cotextToCount.getValue());		// EMITTING THE FINAL KEY/VALUE PAIRS
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "joint occ");
		job.setJarByClass(JointOccurrence.class);
		job.setMapperClass(TokenizerMapper.class);
		// job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MapWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}





/* 

POSSIBLE IMPROVEMENTS

1) 

AT PRESENT THE MAP METHOD, COUNTS THE CO-OCURRENCES OF WORDS IN ONE DOCUMENT AT A TIME AND EMITS A LARGE NUMBER OF INTERMEDIATE KEY/VALUE PAIRS.

THE ABILITY OF THE MAP METHOD TO PRESERVE STATE ACROSS DOCUMENTS TO ACCUMULATE PARTIAL CO-OCCURENCE COUNTS ACROSS DOCUMENTS AND EMIT AN INTERMEDIATE KEY ONLY WHEN ALL THE DOCUMENTS HAVE BEEN PROCESSED CAN BE USED TO IMPLEMENT AN IN-MAPPER COMBINER APPROACH.

THIS CAN BE IMPLEMENTED BY INITIALIZING AN ASSOCIATIVE ARRAY IN A SEPARATE INITIALIZE METHOD THAT IS CALLED BEFORE ANY OF THE MAPPER'S KEY/VALUE PAIRS ARE PORCESSED.

THIS ASSOCIATIVE ARRAY WILL CONTAIN EACH OF THE WORDS ENCOUNTERED IN THE DOCUMENTS ALONG WITH THEIR RESPECTIVE STRIPES AS ENTRIES. WHEN A WORD IS ENCOUNTERED ITS STRIPE IS UPDATED.

IN CASE OF THIS PROGRAM, THE RESULTMAP CAN BE CREATED IN THE INITIALIZE METHOD.  



2) 

THE WHILE LOOP IN THE MAP METHOD CAN BE REPLACED WITH TWO FOR LOOPS. THE FIRST FOR LOOP WILL ITERATE THROUGH THE WORDS IN THE DOCUMENT. AT EACH ITERATION AN ASSOCIATIVE ARRAY WILL BE INITIALIZED.

THE SECOND LOOP WILL BE NESTED IN THE FIRST ONE TO ITERATE THROUGH THE NEIGHBORING WORDS FOR EACH WORD. THE ASSOCIATIVE ARRAY WILL BE UPDATED WITH THE COUNT FOR CO-OCCURENCES AT EACH ITERAION.  

  




*/

