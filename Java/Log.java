/* 

Context:
If you walk through the halls of companies like Facebook, Google, and Amazon, you will see large displays hung on the walls showing trends. For example, at Google you might see the 20 search terms that have been most common in the past 10 minutes; at Facebook, the 10 people which have been friended the most times in the past one hour. These displays update continuously.


Objective:  
Implement a LogProcessor interface that performs part of this functionality. Specifically, you will implement an add(url,time)  method and a getOrderedUrlsInWindow(K) method, which returns a list of the K most commonly viewed URLs in the W seconds previous to the URL with the most recent timestamp. The implementing class constructor creates a LogProcessor class with the specified W.

This code implements this functionality using the Collections package in the LogProcessorFast method and without it in the LogProcessorSlow method.

It makes allowance for cases where calls to add may have values for time that are not always in increasing order. (If add is always called with increasing values for time, then a simple queue suffices.) Another subtlety is that there may be more than one add call with the same page and time.


*/




import java.lang.StringBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;

import static java.lang.Math.max;

import java.util.TreeSet;




import com.google.common.collect.TreeMultiset;
import com.google.common.collect.Multiset;

interface LogProcessor {
  public void add(String url, int time);
  public List<String> getOrderedUrlsInWindow(int K);
}

class LogItem {
  private String url;
  private int time;
  public LogItem(String url, int time) {
    this.url = url;
    this.time = time;
  }
  public String getUrl() {
    return url;
  }
  public int getTime() {
    return time;
  }
  public int compareTo(LogItem li) {
    return time - li.getTime();
  }
  @Override
  public String toString() {
    return url + ", time = " + time;
  }
}

class LogProcessorSlow implements LogProcessor {
  private int W;
  private List<LogItem> liQueue;

  public LogProcessorSlow(int W) {
    this.W = W;
    this.liQueue = new LinkedList<LogItem>();
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer("window size = " + W + "\n");
    for ( LogItem li : liQueue ) {
      result.append( li.toString() + "\n" );
    }
    return result.toString();
  }

  public void add(String url, int time) {
    // //System.out.println("before add:" + this.toString() );
    LogItem anItem = new LogItem(url, time);
    liQueue.add( anItem );
    int mostRecentTime = -1;
    for ( LogItem li : liQueue ) {
      if (li.getTime() > mostRecentTime ) {
        mostRecentTime = li.getTime();
      }
    }
    Iterator<LogItem> liIter = liQueue.iterator();
    while ( liIter.hasNext() ) {
      if (mostRecentTime - liIter.next().getTime() > W ) { 
        liIter.remove();
      }
    }
    // System.out.println("after add:" + this.toString() );
  }

  public List<String> getOrderedUrlsInWindow(int K) {
    final Map<String,Integer> urlCountMap = new HashMap<String,Integer>();
    for ( LogItem li : liQueue ) {
      if ( urlCountMap.containsKey(li.getUrl())) {
        int count = urlCountMap.get(li.getUrl());
        urlCountMap.put(li.getUrl(), count + 1);
      } else {
        urlCountMap.put(li.getUrl(), 1);
      }
    }
    List<String> countArray = new ArrayList<String>(urlCountMap.keySet());
    Collections.sort( countArray, 
      new Comparator<String>() {
        @Override
        public int compare(String s0, String s1) {
          int tmp = urlCountMap.get( s0 ) - urlCountMap.get( s1 );
          if ( tmp != 0 ) {
            return tmp;
          } else {
            // want lexicographically first strings to appear first when tied
            return -s0.compareTo(s1);
          }
        }
      });
    List<String> result = new ArrayList<String>();
    for ( int i = countArray.size()  - 1; i >= max(countArray.size() - K, 0 ); i-- ) {
      result.add( countArray.get(i) + ":" + urlCountMap.get( countArray.get(i) ) );
    }
    return result;
  }

}

class PageTime implements Comparable {
	  String url;
	  int time;

	  public int compareTo(Object o) {
	    PageTime pt = (PageTime) o;
	    int diff = time - pt.time;
	    if ( diff != 0 ) {
	      return diff;
	    } else {
	      return url.compareTo( pt.url );
	    }
	  }

	  public String getUrl() {
		    return url;
		  }
	  
	  public int getTime() {
		    return time;
		  }
	  
	  @Override 
	  public String toString() {
	    return url + ":" + time;
	  }

	  @Override 
	  public boolean equals(Object o) {
	    PageTime obj = (PageTime) o;
	    return obj.url == url && obj.time == time;
	  }

	  public PageTime(String url, int time) {
	    this.url = url;
	    this.time = time;
	  }
	}

	class PageCount implements Comparable {
	  String url;
	  int count;
	  public int compareTo(Object o) {
	    PageCount pc = (PageCount) o;
	    // sort by descending counts
	    int diff = -(count - pc.count);
	    if ( diff != 0 ) {
	      return diff;
	    } else {
	      return url.compareTo( pc.url );
	    }
	  }

	  public String getUrl() {
		    return url;
		  }
	  
	  public int getCount()
	  {
		  return count;
	  }
	  
	  public void setCount(int i)
	  {
		  count=i;
	  }
	  
	  @Override
	  public boolean equals(Object o) {
	    PageCount pc = (PageCount) o;
	    return pc.url == url && pc.count == count;
	  }

	  public PageCount(String url, int count) {
	    this.url = url;
	    this.count = count;
	  }
	}


class LogProcessorFast implements LogProcessor {
  TreeMultiset<PageTime> queue;
  TreeSet<PageCount> counts;
  HashMap<String,PageCount> urlToCount;
  int W;

  public LogProcessorFast(int W) {
    queue = TreeMultiset.create();
    counts = new TreeSet<PageCount>();
    urlToCount = new HashMap<String,PageCount>();
    this.W = W;
  }

  public void add(String url, int time) {
	  
	 
	  
	  queue.add(new PageTime(url, time));
	  
	  Multiset.Entry<PageTime> mostRecent = queue.lastEntry();
	  PageTime pt=mostRecent.getElement();
	  
	  Iterator<PageTime> itr=queue.iterator();
		 while(itr.hasNext())
		 {
			 if((pt.getTime() - itr.next().getTime())>W) 
			 {
				 itr.remove();
			 }
			 else
				 break;
		 }
		
	

	
  }

  public List<String> getOrderedUrlsInWindow(int K) {
	  
	  //  System.out.println( "\n \n  getOrderedIsInWindow  K is " + K + " W is " + W +" \n \n  "  );
	  urlToCount = new HashMap<String,PageCount>();
	  counts = new TreeSet<PageCount>();
	  
	  // populate the Hashmap

	  //System.out.println( "\n \n  now populating urlToCount \n \n ");	  
	  
  Iterator<PageTime> itr1=queue.iterator();
  while(itr1.hasNext())
  {
	  
	  PageTime pt=itr1.next();
	  //System.out.println("element from queue "+ pt.getUrl() + " " + pt.getTime());
	  
	  if(urlToCount.containsKey(pt.getUrl()))
	  {
		  PageCount pc1 = urlToCount.get(pt.getUrl());
		  int count=pc1.getCount();
		  pc1.setCount(count+1);
		  urlToCount.put(pt.getUrl(), pc1);
	  }
	  else
	  {
		  urlToCount.put(pt.getUrl(),new PageCount(pt.getUrl(),1));	  
	  }
	
	//  System.out.println("currently in urlToCount " + urlToCount.get(pt.getUrl()).url + " " + urlToCount.get(pt.getUrl()).count);
	  
  }//end of while
  

	 /* for(PageTime que: queue)
	  {

	  if(urlToCount.containsKey(que.getUrl()))
	  {
	  PageCount count=urlToCount.get(que.getUrl());
	  count.count++;
	  urlToCount.put(que.getUrl(),count);
	  }
	  else
	  {
	  PageCount temp=new PageCount(que.getUrl(), 1);
	  urlToCount.put(que.getUrl(),temp);
	  }
*/
	  
	  
  
  
  // populating counts
  
  //System.out.println( "\n \n  now populating counts \n \n ");
 
  
  
  
  
 // Alternative 1
  
 // Set<PageTime> distQ= new TreeSet<PageTime>(queue.elementSet());

  
  
  Iterator<PageTime> itr3 = queue.iterator();

  while( itr3.hasNext())
  {
  PageTime pt3=itr3.next();
  counts.add(urlToCount.get(pt3.getUrl()));
  }
  
  //dispTS();
 
  
  //after populating the counts tree

  
  //System.out.println( "\n \n  after populating counts \n \n ");
  
  List<String> result = new ArrayList<String>();
  Iterator<PageCount> itr2= counts.iterator();
  int cnt=0;
  while( itr2.hasNext())
	 {
	  
	  if(cnt==K)
		  break;
	  else
	  {
		 cnt++;
	  PageCount pc=itr2.next();
		 result.add(pc.getUrl() + ":" + pc.getCount());
	  }
	 }
	  
  
  //System.out.println( "\n \n  displaying the result after populating counts \n \n ");
  
  
  
/*  for(int i=0; i<result.size(); i++)
	  System.out.println(result.get(i));
  */
  
  //urlToCount = new HashMap<String,PageCount>();
  //System.out.println( "\n \n  END OF FUNCTION  \n \n ");
  
  
	 return result;
  
  }

  
  
  
public void dispQ()
{
	
	Iterator<PageTime> itr=queue.iterator();
	while(itr.hasNext())
	{		
		System.out.println(itr.next().getUrl() );
	}

	System.out.println( "\n");
	
Set<PageTime> distQ= new TreeSet<PageTime>(queue.elementSet());

Collection<PageCount> col=urlToCount.values();
Iterator<PageCount> itr2=col.iterator();
while(itr2.hasNext())
	{
	System.out.println(itr2.next().getUrl()  );
	}


}//end of dispQ



public void dispTS()
{
	
	Iterator<PageCount> itr=counts.iterator();
	while(itr.hasNext())
	{		
		PageCount pc= itr.next();
		System.out.println(pc.getUrl() + " " + pc.getCount());
	}
	
}







}






public class Log
{
	
	public static void main(String[] args)
	{
		LogProcessorFast lp = new LogProcessorFast(1);
		lp.add( "foo", 1 );
	    lp.add( "bar", 2 );
	    lp.add( "foo", 3 );
	    lp.add( "widget", 4 );
	    //lp.add( "foo", 4 );
	   
	   // lp.dispQ();
	    
	    
	    List<String> r = lp.getOrderedUrlsInWindow(3);
	   // System.out.println(r.get(0));
	}
	
}
