import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.text.Document;
import javax.xml.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import java.io.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Government {
	private Map<String,Set<String>>gatherings;
	
	private Map<ArrayList<String>,Integer>timings;
	private Map<String,test>testing=new HashMap<>();
	private String user=null;
	private String password=null;
	private Set<HashSet<String>> seen;
	private Set<HashSet<String>> final_seen;
	private String  database;
	private Connection connection = null;
	private Statement statement = null;
	Government(String configFile){
		 /*Constructor of the Government class. It takes input as the address of configFile which will have the user,
		  * password and Database name(jdbc:mysql://db.cs.dal.ca:3306/nnshukla). These credentials can be used to access
		  * the database of Government. If any of them are null it will be displayed on the console that the user knows.*/
		 Properties prop = new Properties();
		 
         try {
       	  
       	  FileInputStream fis = new FileInputStream(configFile);
          prop.load(fis);
                 
       	
          user=prop.getProperty("user");
          password=prop.getProperty("password");
          database=prop.getProperty("database");
          if(user==null || password==null) {
            	 System.out.println("User or Password is null ");
          
          }
          
          if(database==null) {
            	 System.out.println("Database is null ");
             }

          }
         
          catch(FileNotFoundException e) {
       	  
        	  System.out.println(e.getMessage());
          }
         
          catch (IOException e) {
			// TODO Auto-generated catch block
        	 System.out.println(e.getMessage());
          }
         
          catch (Exception e) {
     			// TODO Auto-generated catch block
          System.out.println(e.getMessage());
		
          } finally {

	            if (connection != null) {
	                try { connection.close(); } catch (SQLException sqlEx) { } // ignore
	                connection = null;
	            }
	        }


	}

	
	
	 private String getCharacterDataFromElement(Element e) {
		 if(e!=null) {
		    Node child = e.getFirstChild();
		    if (child instanceof CharacterData) {
		       CharacterData cd = (CharacterData) child;
		       return cd.getData();
		    }
		    }
		    return "";
		  }
	 
	 
	 
	 
	 
	 
	 
	 
	boolean mobileContact(String initiator, String contactInfo) {
		/*This method is called by SynchronizeData It has two inputs initiator and contactInfo 
		 * And it returns whether the initiator came in contact with someone positive in the 
		 * last 14 days or not.ContactInfo also contains testHashes. It is first checked that if 
		 * the government has already got these in the form of recordTestResult.If they have got 
		 * then the mobile hash of the initiator is put in the row of corresponding testhash.
		 * However, if the testHash is not already available with the government then it is
		 * added to the hashMap testing.*/
		int z=0;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				InputSource is = new InputSource();
		        
				is.setCharacterStream(new StringReader(contactInfo));
		        
				org.w3c.dom.Document doc;
				
				doc = db.parse(is);
			    
				NodeList nodes = doc.getElementsByTagName("tests");
			    // iterate the employees
		        PreparedStatement st1;
				
					
					
	  	        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	  	             // Connect to the database
	  		    connection = DriverManager.getConnection(database,user,password);

	  		            // Create a statement
	  		    statement = connection.createStatement();
								    
	  		      //TestHashes are extracted from xml string       
		        for (int i = 0; i < nodes.getLength(); i++) {
		           
		        	Element line = (Element) nodes.item(i);
	
		           
		        	test temp_test=testing.getOrDefault(getCharacterDataFromElement(line), null); 
		      
		           //If recordTestResult did not get the testHash already then add it to the HashMap
		        	if(temp_test==null) {
		      
		        	   testing.put(getCharacterDataFromElement(line),new test(initiator,true,-999));
		           
		           
		        	}
		           //If it has recieved it then insert it to the database.
		        	else {
		        	
		        	
		        		PreparedStatement st5 = connection.prepareStatement("INSERT IGNORE INTO Devices SET mob_hash = '"+initiator+"';");
					
		        	   
		        		st5.executeUpdate();
		        	
		        	   
		        		PreparedStatement st2=connection.prepareStatement("UPDATE tests SET mob_hash='"+initiator+"' , date_test="+temp_test.date+", results="+temp_test.result+" WHERE TestHash='"+getCharacterDataFromElement(line)+"';");
					
		        	   
		        		st2.executeUpdate();
		        	   
		           }
		           
		        }
		        
		        
		        NodeList nodes1 = doc.getElementsByTagName("contact");
			    // iterate the employees
			    //Extract all the info about individual hash,date,duration etc.
		        for (int i = 0; i < nodes1.getLength(); i++) {
		        
		        	Element element = (Element) nodes1.item(i);
		           
		        	NodeList name = element.getElementsByTagName("individual");
		           
		        	NodeList Date = element.getElementsByTagName("Date");
		           
		        	NodeList Duration = element.getElementsByTagName("Duration");
		           
		        	Element line = (Element) name.item(0);
		           
		        	Element line1 = (Element) Date.item(0);
		           
		        	Element line2 = (Element) Duration.item(0);
		           
		        	List<String> temp=new ArrayList<String>();
		           
		        	temp.add(initiator);
		           
		        	temp.add(getCharacterDataFromElement(line));
		           
		           //making the entry to the database of teh unique mobilehahses in table Devices.

					st1 = connection.prepareStatement("INSERT IGNORE INTO Devices SET mob_hash = '"+temp.get(0)+"';");
					st1.executeUpdate();
					st1 = connection.prepareStatement("INSERT IGNORE INTO Devices SET mob_hash = '"+temp.get(1)+"';");
					st1.executeUpdate();
					
					PreparedStatement st2=connection.prepareStatement("INSERT IGNORE INTO Contacts SET User1_MobileHash='"+temp.get(0)+"',User2_MobileHash='"+temp.get(1)+"',date_meet='"+Integer.parseInt(getCharacterDataFromElement(line1)) +"',duration="+Integer.parseInt(getCharacterDataFromElement(line2))+",shown=0;");
					st2.executeUpdate();
					
					
					
					
					
					
					ResultSet result_for_query1 = null;
				    //Running a query for obtaining the info about the people who came in contact with the initiator within last 14 days. 	
					PreparedStatement st3=connection.prepareStatement("select tests.TestHash as hash,Contacts.User2_MobileHash as user2, Contacts.User1_MobileHash as user1 from ((Contacts inner join Devices on Devices.mob_hash=Contacts.User2_MobileHash)inner join tests on tests.mob_hash=Devices.mob_hash) where Contacts.User1_MobileHash='"+initiator+"' and abs(Contacts.date_meet-tests.date_test)<14 and tests.results=1 and shown=0;");
					result_for_query1=st3.executeQuery();
					
					while(result_for_query1.next()) {
						z+=1;
						PreparedStatement st4=connection.prepareStatement("UPDATE Contacts SET shown=1 WHERE User2_MobileHash='"+result_for_query1.getString("user2")+"' and User1_MobileHash='"+result_for_query1.getString("user1")+"';");
						st4.executeUpdate();
					}
		        
		        }
		        
	    }
				
	    
	    catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
				} 
	    
	    catch (Exception ex) {
					System.out.println(ex.getMessage());
	  	        
				}
					
		   
		        
				
		       
			
	    if(z>0) {
	    	
		  //If there are more then 1 then return True else return false
	    	return true;
		
	    }
	        
	    else {
	    	
	        	return false;
	        
	    }
		
	}
	
	

	
	
	
	void recordTestResult(String testHash, int date, boolean result) {
		/*This method will be directly called by the object of Government class
		 * date is the date on which the testing is done and result is the result of the test.
		 * There can be two cases here either the MobileDevice has already sent this testHash 
		 * to the government class through mobileContact. In that case we can make the entry in tests table 
		 * of the testHash and the the mobileHash of that and the true result(False result will not be stored)
		 * and it is removed from the testing hashMap.
		 * The other case is that the mobile device user is yet to send the testHash. In that case we store the
		 * testHash and the information regarding to that in the hashMap testing which has a key of testHash and
		 * the value is the object of class testing. Here we will store the mobile Hash as null as we are yet 
		 * to know that who this testHash belongs to. And then we make an entry in the tests table where mob_Hash
		 * field is kept as null until the mob_Hash whose test it is sends it.  
		 * */
		
		if(testHash==null||testHash.isBlank()||testHash.isEmpty()) {//If testHash is null or empty nothing will be done.
			return;
		}
		test temp=testing.getOrDefault(testHash, null);//Retrieving the information about the test if the testHash received
		//by the government is present in the hashMap. Else null is returned 
		
           
		try {
  	    
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
 	        
			// Connect to the  database
 		    
			connection = DriverManager.getConnection(database,user,password);

 		            // Create a statement
 		    
			statement = connection.createStatement();

		
			if(temp!=null && result==true) {//If there is information about this testHash already received(from mobileDevice through mobileContact) and the result is true  
			

				PreparedStatement st1=connection.prepareStatement("INSERT IGNORE INTO Devices SET mob_hash = '"+temp.mob_hash+"';");
				
				st1.executeUpdate();
				
				PreparedStatement st2=connection.prepareStatement("INSERT IGNORE INTO tests SET TestHash='"+testHash+"', mob_hash='"+temp.mob_hash+"' , date_test='"+date+"', results="+result+";");
				
				st2.executeUpdate();
				
				testing.remove(testHash);	
				
			}
		
			else if(temp==null && result==true) {//If temp is null i.e. there is no information about the testHahs previously available 
			
				testing.put(testHash, new test(null,true,date));//Put the info about the hahsmap with the mobileHahs as null
			
				PreparedStatement st3;
		
				st3 = connection.prepareStatement("INSERT IGNORE INTO tests SET TestHash='"+testHash+"',mob_hash="+null+",date_test='"+date+"',results="+result+";");
 				
				st3.executeUpdate();//Update the information in the database
			
			}
			
		}
		catch (SQLException e) {
		
		
			System.out.println(e.getMessage());
			
		}
		catch (InstantiationException e) {
		
		
				System.out.println(e.getMessage());
		
				
		} 
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			
			System.out.println(e.getMessage());
		
		}
		catch (ClassNotFoundException e) {
		
			// TODO Auto-generated catch block
			
			System.out.println(e.getMessage());
		
		}
		   
		
		
		
	
	}

		
		
		
	
	
	
	
	
	
	
	
	
public int findGatherings(int date, int minSize, int minTime, float density) {
		/*This method is responsible for finding the gathering on a particular date for atleast some minimum duration.
		 *It returns the number of Large gatherings only. So in this method method I have taken each pair like a and b
		 *and then the intersection of all the members that came in contact with a and b is taken even a and b is included
		 *to this group after that it is checked that how many members in this group came in contact with each other.
		 *Only groups larger than minSize are considered. 
		 *The total number of contacts is c and m is the maximum number of contact that can occur in all the members so 
		 *m=n*(n-1)/2 where n is the size of the group. If c/m>density then only it is reported back as large gathering
		 *Please note that sub-gatherings will not be recorded here. This means that {a,b,c,d} and {a,b,c} won't be 
		 *considered different.However {a,b,c,d} and {a,b,e,f} will be considered different this is because consider
		 *a situation in which a,b meet with c,d and then on the same day they met with e,f or any other bigger gathering
		 *so it should be considered.Please note that if two members have met at two different durations then the durations 
		 *will be added. 
		 * */
	        seen=new HashSet<>();
	        final_seen=new HashSet<>();
	        timings=new HashMap<>();
	        gatherings=new HashMap<>();
		    if(minSize<=0 ||minTime<=0||density<=0) {
		    	return 0;
		    }
		  
			try {
				
				
	            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	             // Connect to the database
		            connection = DriverManager.getConnection(database,user,password);

		            // Create a statement
		        statement = connection.createStatement();
				Set<ArrayList<String>> temp=new HashSet<>();
				//Query for retrieving the users who met on that particular date.
				PreparedStatement statement=connection.prepareStatement("select User1_MobileHash, User2_MobileHash,SUM(duration) AS duration from Contacts where date_meet="+date+" GROUP BY User1_MobileHash, User2_MobileHash;");
				ResultSet result_for_query1 = statement.executeQuery();
				while(result_for_query1.next()) {
					//Storing the members who met each other
					ArrayList <String> x=new ArrayList<String>();
					String a=result_for_query1.getString("User1_MobileHash");
					String b=result_for_query1.getString("User2_MobileHash");
					
					Set<String>value=gatherings.getOrDefault(a, new HashSet<String>());
					value.add(a);
					value.add(b);
					Set<String>value1=gatherings.getOrDefault(b, new HashSet<String>());
					value1.add(a);
					value1.add(b);
				    x.add(a);
					x.add(b);
					gatherings.put(a,value);
					gatherings.put(b,value1);
					Collections.sort(x);//to ensure that unique contacts are stored
					timings.put(x,result_for_query1.getInt("duration"));//storing the duration of the meeting
					temp.add(x);
					
				}
				
				
				Iterator<ArrayList<String>> itr=temp.iterator();
				while(itr.hasNext()) {
					int c=0;
					ArrayList<String> temp2=itr.next();
					String a=temp2.get(0);
					String b=temp2.get(1);
					HashSet<String>S=new HashSet<String>();
					S.addAll(gatherings.get(a));
					HashSet<String>S1=new HashSet<String>();
							S1.addAll(gatherings.get(b));
							
							ArrayList<String> members=new ArrayList<>();		
							int n=0;
					if(S.size()>S1.size()) {
						S.retainAll(S1);//Intersection of two sets
						n=S.size();
						if(S.size()>=minSize && !seen.contains(S)) {
							
							members.addAll(S);//storing members to arraylist
							
					}
						}
					else {
						S1.retainAll(S);
						n=S1.size();
						if(S1.size()>=minSize && !seen.contains(S1)) {
							members.addAll(S1);
					
						}
					
				
				
				
				
					}
						
					for(int i=0;i<members.size()-1;i++) {
							for(int j=i+1;j<members.size();j++) {
								ArrayList<String>check_time=new ArrayList<>();
								check_time.add(members.get(i));
								check_time.add(members.get(j));
								Collections.sort(check_time);
							    if(gatherings.get(members.get(i)).contains(members.get(j))&& timings.getOrDefault(check_time, 0)>=minTime) {//checking if there was an interaction between two members and the time of interaction is greater then minTime
							    	c+=1;//incrementing c
							    
							    }  	
								
							
							}
							
						}	
							
						
						
					
						double m=(double)n*(n-1)/(double)2;
						if((double)c/(double)m>density) {
							HashSet<String>members_gathering=new HashSet<>();
							members_gathering.addAll(members);
							seen.add(members_gathering); //All the possible large gatherings
							
						
						}
			}
		final_seen=doSubsetFiltering(seen); 		//Removing the subgatherings.
				
				
					
		}
				
			catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
	        }
				
	   

		
		
		
	return final_seen.size();//Returning the size of gatherings
	
	

}





private HashSet<HashSet<String>> doSubsetFiltering(Set<HashSet<String>> seen2) {
	//This method is responsible for removing the subgatherings 
	// TODO Auto-generated method stub
	List<HashSet<String>> removedIs=new ArrayList<>();
	List<HashSet<String>> ListIs=new ArrayList<>();
	ListIs.addAll(seen2);
	for (int i = 0; i < ListIs.size()-1; i++) { 
		HashSet<String> thisIs = ListIs.get(i);
         // i+1, the checking starts from the next IntegerSet
        for (int j = i+1; j < ListIs.size(); j++) { 
        	HashSet<String> nextIs = ListIs.get(j);
            if (thisIs.containsAll(nextIs)) {
                // To remove thisIs set as it is a subset of isNext
                removedIs.add(nextIs); 
                
             }
            else if(nextIs.containsAll(thisIs)) {
            	removedIs.add(thisIs); 
                
            }
         } // inner-for-loop
    } // outer for-loop
    ListIs.removeAll(removedIs);
    HashSet<HashSet<String>>remove_set=new HashSet<>();
    remove_set.addAll(ListIs);
    return remove_set;
	
}
}
