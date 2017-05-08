
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Session {
	static ArrayList<String> deviceID_arr=new ArrayList<String>();
	static ArrayList<RequiredData> rd_arr=new ArrayList<RequiredData>();
	static ArrayList<String> gameID_arr=new ArrayList<String>();
	
	public static void attribute_extraction(JSONObject ob)
	{
	  String device="",event="",timestamp="",game_id="";
	  JSONObject head=(JSONObject) ob.get("headers");//Extracting JSON object
	  device=(String) head.get("ai5");//
	  JSONObject post=(JSONObject) ob.get("post");//Extracting JSON object
	  event=(String) post.get("event");
	  JSONObject bottle=(JSONObject) ob.get("bottle");//Extracting JSON object
	  timestamp=(String) bottle.get("timestamp");
	  game_id=(String) bottle.get("game_id");
	  
	  if(!deviceID_arr.contains(device))//Storing distinct "ai5" in deviceID_arr
		  deviceID_arr.add(device) ;
	  if(!gameID_arr.contains(game_id))//Storing distinct "game_id" in gameID_arr
		  gameID_arr.add(game_id) ;
		  
	  rd_arr.add(new RequiredData(device,event,timestamp,game_id));
	  
	  
	}
	/*** Method to calculate the time in seconds ***/
	public static double time_calculation(String ts)
	{
		String str="",date="",tm="";
		double hr=0,min=0,sec=0,s=0;
		int l=0,m=0,y=0;
		int x=ts.length();
		
		for(int i=0;i<x;i++)
		{
			if(ts.charAt(i)==' ')
			{
				l=i+1;
				
			}
		}
		String time=ts.substring(l,x);
		
		int n=time.length(),c=0;
		for(int i=0;i<n;i++)
		{
			if(time.charAt(i)==':')
			{
				++c;
				if(c==1){
					m=i+1;
					tm=time.substring(0,i);
					hr=Double.parseDouble(tm);
					sec=hr*3600;
				}
				if(c==2){
					y=i+1;
					tm=time.substring(m,i);
					min=Double.parseDouble(tm);
					sec=sec+(min*60);
				}
				
			}
		}
		tm=time.substring(y,n);
		s=Double.parseDouble(tm);
		sec=sec+s;
		
		return sec;//returning the time in seconds
	}
	
	/*** Method to calculate the session, total number sessions, total number of valid sessions and 
	  				average valid session time***/
	public static Map<String,CountSessions> session_calculate()
	{
		double v=0,d=0,z=0,sum=0,average=0,sumtot=0,avg=0,t=0,h=0;
		String p="",str="";
		long count_total=0,count_valid=0,e=0,k=0,dataloss=0,dcount=0,gcount=0,max=0,max1=0,n=0,count_total1=0,count_valid1=0,count_sum=0;
		Map<String,CountSessions> gameSessions=new Hashtable<String,CountSessions>();
		
		/* Finding the most popular game */
		
		for(String g : gameID_arr)
		{
			gcount=0;
			for(RequiredData rd : rd_arr)
			{
				if(rd.game_id.equals(g))
				{
					
					++gcount;
					if(gcount>max1)
					{
						max1=gcount;
						str=rd.game_id;
					}
				}
			}
		}
		
		/* Finding the session, total number sessions, total number of valid sessions and data losses */
		
		for(String s : deviceID_arr)
		{
			String device="",event="",timestamp="",game_id="";
			count_valid=0;count_total=0;sum=0;
			int f=0;
			k=0;
			dcount=0;
			String gId="";
			for(RequiredData rd : rd_arr)
			{
				
				if(rd.device.equals(s))
				{
					gId=rd.game_id;
					//System.out.println("game id "+gId);
					++dcount;
					if(dcount>max)
					{
						max=dcount;
						p=rd.device;  //Finding the most active device
					}
					if(f==0)
					{	
						z=time_calculation(rd.timestamp);
						n=1;
						f=1;
					}
					
					
					if(rd.event.equals("ggstart")){
						
						if(k==0)
							k=1;
						else
						{
							
							v=time_calculation(rd.timestamp);
							
							if((v-t)>30)
							{
								if(d==0)
								{
									
									t=v;
									continue;
								}
								else
								{
									d=d+.0001;
									++count_total;
									if(d>60)
									{
										sum=sum+d;
										++count_valid;
									}
									if(d<1)
									{
										--count_total;
									}
									d=0;
									t=v;
									continue;
								}
							}
							else
							{
								d=d+.0001;
								t=v;
								continue;
								
							}
						}
						
						v=time_calculation(rd.timestamp);
						t=v;
						
						if((v-z)>30)
						{
							
							++count_total;   //counting total sessions
							if(d>60)
							{
								sum=sum+d;
								++count_valid;  //counting valid sessions
							}
							if(d<1)
							{
								--count_total;  
							}
							d=0;
						}
					}
					if(rd.event.equals("ggstop"))
					{
						if((k==0)&&(n==1))
						{
							z=time_calculation(rd.timestamp);
							h=z;
							n=0;
							continue;
						}
					    if(k==1)
					    	k=0;
					    else
					    {
					    	
					    	z=time_calculation(rd.timestamp);
							if((z-h)>30)
							{
								if(d==0)
								{
									h=z;
									continue;
								}
								else
								{
									d=d+.0001;
									++count_total;
									if(d>60)
									{
										sum=sum+d;
										++count_valid;
									}
									if(d<1)
									{
										--count_total;
									}
									d=0;
									h=z;
									continue;
								}
							}
							else
							{
								d=d+.0001;
								h=z;
								continue;
								
							}
					    	
					    }
						z=time_calculation(rd.timestamp);
						h=z;
						d=d+(z-v);
						n=0;
					}
					  
				}
				
			}
			if(k==1)
			{
				//++dataloss;
				d=d+.0001;
			}
			if(d<1)
			{
				d=0;
			}
			else if(d>=1)
			{
				++count_total;
				if(d>60)
				{
					sum=sum+d;
					++count_valid;
				}
				d=0;
			}
			
			count_total1+=count_total;
			count_valid1+=count_valid;
			count_sum+=sum;
			/***Stores data for each game Id**/
		    if(gameSessions.containsKey(gId))
		    {
		    	CountSessions cs=(CountSessions)gameSessions.get(gId);
		    	long valSes=cs.getValidSession();
		    	valSes+=count_valid;
		    	cs.setValidSession(valSes);
		    	
		    	long totSes=cs.getTotalSession();
		    	totSes+=count_total;
		    	cs.setTotalSession(totSes);
		    	
		    	double totSessionTime=cs.getTotalSessionTime();
		    	totSessionTime+=sum;
		    	cs.setTotalSessionTime(totSessionTime);
		    	
		    	gameSessions.put(gId,cs);
		    }
		    else
		    {
		    	
		    	gameSessions.put(gId, new CountSessions(count_valid,count_total,sum));
		    }
		    
		}
		
		if(count_valid1!=0)
		 average=count_sum/count_valid1;
		else
		 average=0;	
		System.out.println("The game with game_id: \""+str+"\" is the most popular one!!");
		System.out.println (" ");
		System.out.println("The device with ai5: \""+p+"\" is the most active one!!");
		System.out.println (" ");
		
		System.out.println("Total number of sessions: "+count_total1);
		System.out.println (" ");
		
		System.out.println("Total number of valid sessions: "+count_valid1);
		System.out.println (" ");
		System.out.println("The average session time for all the valid sessions: "+average);
		return gameSessions;
		
	}
	
	/** The main method **/
	
	public static void main(String args[])throws FileNotFoundException, IOException, ParseException
	{
		
		/** Opening the data dump log file in read mode **/
		FileInputStream in = new FileInputStream("C:/Users/Admin/Desktop/ggevent.log");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		JSONParser parser = new JSONParser();  //Creating JSON parser object
		Map<String,CountSessions> gameSessions=new Hashtable<String,CountSessions>();
		
		String str="";
		
		
		/** Reading contents from the log file **/ 
		while ((str = br.readLine()) != null)   {
			  
			
			  JSONObject json = (JSONObject) parser.parse(str); //JSON parsing
			  
			  attribute_extraction(json);
			  
			  //c++;
			}
		
		br.close();  //Closing file
		
		System.out.println (" ");
		
		gameSessions=session_calculate();  //Calling session_calculate method
		System.out.println("*************************The number of sessions (valid and total) of each game and the average session***************************");

		for(Map.Entry<String,CountSessions> entry:gameSessions.entrySet()){    
	        String key=entry.getKey();  
	        CountSessions cs=entry.getValue();  
	        System.out.println("Game ID "+key+" Details:");  
	      
	        System.out.print(" valid session : "+cs.validSession+" total session : "+cs.totalSession);
	        if(cs.validSession!=0)
	        {
	        	System.out.println(" average valid session time : "+(cs.totalSessionTime/cs.validSession)); 
	        	cs.setAverageSessionTime(cs.totalSessionTime/cs.validSession);
	        }
	        else
	        {
	        	System.out.println(" average valid session time : 0");
	        	cs.setAverageSessionTime(0);
	        }
	        gameSessions.put(key,cs);
	    } 
		
		
	}
}