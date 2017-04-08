import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Features 
{
	public Map<String, List<User>> cacheData;
	public List<User> users;

	
	
	
	/*This method reads the input text file and populates the 
	* Map record (cacheData) by Iterating over each and every 
	* line in the log file (.txt) 
	* @param fileName
	*/
	
	public  void readFileAndPopulateCache(String fileName) 
	{
		try 
		{
			int counter=0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY:HH:mm:ss Z");
			cacheData = new HashMap<String, List<User>>();
			//String records[]= {};
			String line = "";
			User user=null;
			
	        // Create the object of BufferedReader object
	        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));   
	        BufferedWriter brw1= new BufferedWriter( new OutputStreamWriter(new FileOutputStream("./log_output/hosts.txt ")));
	       
	        
	        // Read one line at a time 
	        
	        while((line = br.readLine()) !=null)
	        {   
	       	//records=line.split("(\\S+) - - \\[(\\S+ -\\d{4})\\] \"(.+)\" (\\d+) (\\d+|-)");
	       	Pattern pattern = Pattern.compile("(\\S+) - - \\[(\\S+ -\\d{4})\\] \"(.+)\" (\\d+) (\\d+|-)");
	            Matcher matcher = pattern.matcher(line);
	            if(matcher.matches()) 
	            {
	           	user= new User();
	           	String bw=matcher.group(5);
	           	if (bw.equals("-"))
	           	{
	           		bw="0";
	           	}
	           	user.ipAddress=matcher.group(1);
		       	user.statusCode= Integer.parseInt(matcher.group(4));
		       	user.bandwidth=Long.parseLong(bw);
		       	user.loginTime=sdf.parse(matcher.group(2));
		       	user.url=matcher.group(3);
		       	 
		       		// populating the Map with key as uId and List of Users as values 
						// If the uid is already present values are saved to existing key
						// else new key value pair is created
						if(cacheData.containsKey(user.ipAddress))
						{
							cacheData.get(user.ipAddress).add(user);
						}
						else
						{
							users = new ArrayList<User>();
							users.add(user);
							cacheData.put(user.ipAddress, users);

						}
					
	             }
	       	 
	            counter++; 
	       	 
	        }
	        
	        System.out.println(user.loginTime);
	        System.out.println(user.ipAddress);
	        System.out.println(user.bandwidth);
	        System.out.println(user.statusCode);
	        System.out.println("Size of Map"+ cacheData.size());
			

	        System.out.println(counter);
	        sortCachedData(cacheData);
	        System.out.println("Sorted");
		} 

		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (ParseException  e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}

	public void sortCachedData(Map<String, List<User>> map)
	{
		Set<Entry<String, List<User>>> set = map.entrySet();
	   List<Entry<String, List<User>>> list = new ArrayList<Entry<String, List<User>>>(set);
	   Collections.sort( list, new Comparator<Map.Entry<String, List<User>>>()
	   {
	       public int compare( Map.Entry<String, List<User>> o1, Map.Entry<String, List<User>> o2 )
	       {
	           return (o2.getValue().size()-o1.getValue().size());
	       }
	   } );
	   
	   for (int i = 0; i < 10 && i < list.size(); i++) {
       System.out.println(list.get(i));
   }
   
	   
	}
	

		 
	
	public static void main(String[] args)
	{
		Features solution = new Features();
		String fileLocation="./log_input/log.txt ";
		Long initialTime= System.currentTimeMillis();
		solution.readFileAndPopulateCache(fileLocation);
		Long timeTaken=((System.currentTimeMillis()- initialTime)* 1000)/60;
	}

	

}
/*
 * Bean class 
 * 
 */
class User
{
	String ipAddress;
	Date loginTime;
	Long bandwidth;
	Integer statusCode;
	String url;
}
