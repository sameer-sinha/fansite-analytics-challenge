package insightChallenge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Features
{

	public List<User> users= new ArrayList<>() ;
	public Map<String, Long> resources;
	public Stack<String> topTenElements;
	public Map<String, Long> loginFrequecy;
	public Map<String, Long> activeIps;
	public BufferedWriter writer=null;
	public Map<String, List<User>> blockedIps;

	public  void readFileAndPopulateCache(String fileName) 
	{
		try 
		{

			//String records[]= {};
			
			String line = "";
			User user=null;

			// Create the object of BufferedReader object
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));


			// Read one line at a time 

			while((line = br.readLine()) !=null)
			{   
				Pattern pattern = Pattern.compile("(\\S+) - - \\[(\\S+ -\\d{4})\\] \"(.+)\" (\\d+) (\\d+|-)");
				Matcher matcher = pattern.matcher(line);

				if(matcher.matches()) 
				{
					String bw=matcher.group(5);
					if (bw.equals("-"))
					{
						bw="0";
					}
					String ipAddress=matcher.group(1);
					Integer statusCode= Integer.parseInt(matcher.group(4));
					Long bandwidth=Long.parseLong(bw);
					String loginTime = matcher.group(2);
					String url=matcher.group(3);
					user= new User(ipAddress, loginTime, bandwidth, statusCode, url);
					users.add(user);
				}
			}		         
			feature1();
			feature2();
			feature3();
			feature4();			

			br.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	// Implementation of First Feature 

	private void feature1() {
		activeIps =
				users.parallelStream().collect(
						Collectors.groupingBy(
								User::getIpAddress, Collectors.counting()
								)
						);

		topTenElements = getTopNResources(activeIps,10);
		String fileName = "./log_output/hosts.txt";
		writeToText(fileName, true, activeIps);
	}


	// Implementation of Second Feature

	private void feature2() 
	{
		resources = users.parallelStream()
				.collect(Collectors.groupingBy(User::getUrl,
						Collectors.summingLong(User::getBandwidth)));

		topTenElements = getTopNResources(resources,10);
		String fileName = "./log_output/resources.txt";
		writeToText(fileName, false, null);
	}


	// Implementation of third feature 

	private void feature3() 
	{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY:HH:mm:ss");
			loginFrequecy= new HashMap<String, Long>();
			Date loginTime=null;
			String lT="";
			long diffMinutes=0l;
			long counter=0l;
			for (int i=0; i< users.size();i++) 
			{

				Date tempLoginTime= sdf.parse(users.get(i).getLoginTime().substring(0,(users.get(i).getLoginTime().length()-6)));
				String tempLT=users.get(i).getLoginTime();

				if(null!=tempLoginTime&&null!=loginTime)
				{
					Instant end = Instant.ofEpochMilli(tempLoginTime.getTime());
					Instant start = Instant.ofEpochMilli(loginTime.getTime());
					Duration difference= Duration.between(start, end);
					diffMinutes=difference.toMinutes();
				}


				if(null==loginTime||diffMinutes>60)
				{
					loginFrequecy.put(tempLT, counter++);
					counter=0l;
					loginTime=tempLoginTime;
					lT=tempLT;
				}
				else
				{
					loginFrequecy.put(lT, counter++);
				}

			}

			topTenElements  = getTopNResources(loginFrequecy,10);			
			String fileName = "./log_output/hours.txt";
			writeToText(fileName, true, loginFrequecy);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	// Implementation of fourth feature 


	private void feature4()
	{

		String fileName="./log_output/blocked.txt";
		blockedIps =
				users.parallelStream()
				.filter(s->s.getStatusCode().toString().startsWith("4"))
				.collect(Collectors.groupingBy(User::getIpAddress,
						LinkedHashMap::new,
						Collectors.toList()));

		writeToText(fileName);



	}

	private void writeToText(String fileName)
	{
		try 
		{

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName))));

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY:HH:mm:ss Z");

			for (String key : blockedIps.keySet()) 
			{

				List<User> usersList = blockedIps.get(key);
				if (null != usersList) 
				{
					int loginAttempt=0;
					long diffMinutes=0l;
					long diffSeconds=0l;

					Date initialTime=null;
					Instant start=null;     

					for (int i = 0; i < usersList.size(); i++)
					{


						if(loginAttempt==3)
						{
							initialTime= sdf.parse(usersList.get(0).getLoginTime());
							Date thirdAttemptTime = sdf.parse(usersList.get(i).getLoginTime());
							start= Instant.ofEpochMilli(initialTime.getTime());
							Instant end = Instant.ofEpochMilli(thirdAttemptTime.getTime());
							Duration difference= Duration.between(start, end);
							diffSeconds=difference.getSeconds();
							if(diffSeconds<20)
							{

								writer.write(usersList.get(i).getIpAddress() + 
										" - - [" + usersList.get(i).getLoginTime() +"] " + "\"" +
										usersList.get(i).getUrl() +"\" " + usersList.get(i).getStatusCode() + 
										" " +usersList.get(i).getBandwidth() +"\n");

							}
						}
						if(loginAttempt>3)
						{
							Date endTime=sdf.parse(usersList.get(i).getLoginTime());
							Instant end = Instant.ofEpochMilli(endTime.getTime());
							Duration difference= Duration.between(start, end);
							diffMinutes =difference.toMinutes();

							if(diffMinutes<5)
							{
								writer.write(usersList.get(i).getIpAddress() + 
										" - - [" + usersList.get(i).getLoginTime() +"] " + "\"" +
										usersList.get(i).getUrl() +"\" " + usersList.get(i).getStatusCode() +
										" " +usersList.get(i).getBandwidth() +"\n");
							}
						}

						if(diffMinutes>5)
						{
							loginAttempt=0;
						}

						loginAttempt++;
					}
				}

			}


			writer.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}




	public void writeToText(String fileName, boolean flag, Map<String, Long> elementsMap)
	{
		try {

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName))));

			while(!topTenElements.isEmpty())
			{
				String resource = topTenElements.pop();

				if(flag){

					writer.write(resource+","+elementsMap.get(resource)+"\n");
				}

				else
				{
					String[] resourseArray = resource.split("\\s+"); 
					writer.write(resourseArray[1]+"\n");
				}
			}

			writer.close();
		} 
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static Stack<String> getTopNResources(final Map<String, Long> map, int n) 
	{
		PriorityQueue<String> topN = new PriorityQueue<String>(n, new Comparator<String>() 
		{
			public int compare(String s1, String s2)
			{
				return Long.compare(map.get(s1), map.get(s2));

			}
		});

		for(String key:map.keySet()){
			if (topN.size() < n)
				topN.add(key);
			else if (map.get(topN.peek()) < map.get(key)) {
				topN.poll();
				topN.add(key);
			}
		}

		Stack<String> topStack = new Stack<String>();	    

		while(!topN.isEmpty())
		{
			topStack.push(topN.poll());
		}

		return topStack;
	}



	public static void main(String[] args)
	{
		Features solution = new Features();
		String fileLocation="./log_input/log.txt ";
		solution.readFileAndPopulateCache(fileLocation);
	}

}
