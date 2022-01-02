import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MobileDevice {
	List<String>testHash =new ArrayList<>();
	Map<String,String>configurations=new HashMap<>();
	public String deviceHash;
	String address;
	String user=null;
	String deviceName=null;
	boolean flag_null_input=false;
	List<Contact> contact_list=new ArrayList<>();
	Government contactTracer;
	MobileDevice(String configFile, Government contactTracer){
		/*Constructor off MobileDevice. It takes two inputs first is the path of the configFile second is the 
		 *object of Government class. Through this object methods of Government class can be invoked in class 
		 * MobileDevice. However, if any of the user, deviceName is absent or could not be retrieved for some 
		 * reason then no other method will work too. This is done to avoid sending incomplete data to the 
		 * government database. And the user will be notified that his data could not be taken due to some 
		 * error.*/
		this.contactTracer=contactTracer;
		Properties prop = new Properties();
	    if(configFile!=null) {    	  	
			
	    	try {        	    
	        
				FileInputStream fis = new FileInputStream(configFile);
	        	// load from input stream
	            prop.load(fis);

	        	
	            user=prop.getProperty("user");
	            deviceName=prop.getProperty("deviceName");
	              
	        
			}
			
			catch(FileNotFoundException e) {
	        	  user=null;
	              deviceName=null;
	        
			
			} 
		
			catch (IOException e) {
				// TODO Auto-generated catch block
	        	  user=null;
	              deviceName=null;
			
			
			}	
			  
	          
	          
	    }
	    
	    
	    if(user==null|| deviceName==null) {
	        	  
	        	  System.out.println("Either user name or Devicename could not be recieved");
	        	
	        	  flag_null_input=true;
	          
	        
	          }
	          
		
	}

	
	void recordContact(String individual, int date, int duration) {
		/*This method takes in the hash of individual, date and the duration of the contact. and stores it 
		 *in the mobile Device. Please note that it does not store it in the government database but in the
		 *local memory of the mobileDevice. The anticipated error that can occur in this method may be due to
		 *wrong or out of bound input parameters so they have been handled. And in case of error the data will
		 *not be stored.*/
		if(individual==null||individual.isBlank()||individual.isEmpty()||flag_null_input==true||duration<0) {
		
			return;
		
		}
		try {
		
			contact_list.add(new Contact(individual,date,duration));
		
		}
		
		catch(Exception e) {
			
			System.out.println(e.getMessage());
		
		}
	}
	
	void positiveTest(String testHash) {
		
		/*In this method an alphanumeric testHash will be sent by the testing Agency. 
		 *And it will be stored in the mobileDevice initially for that person till his 
		 *mobile Synchronizes. The anticipated error that can occur in this method is 
		 *that testHash is null or blank. and other is that this method is called for
		 *an object for which the user or DeviceName were not properly obtained. All 
		 *these cases have been handled.
          */
	
		if(flag_null_input==true||testHash==null||testHash.isEmpty()||testHash.isBlank()) {
		
			
			System.out.println("Test cannot be recorded");
			return;
		
		
		}
		
		try {
		
			
			this.testHash.add(testHash);
		
		}
		
		catch(Exception e) {
		
		
			System.out.println("Test cannot be recorded");
		}
	}
	
	
	
	boolean synchronizeData() {
		/*This method is responsible for returning to a user if 
		 *he has come in contact with someone who tested positive 
		 *in 14 days of their contact. This method is responsible 
		 *for sending class by calling the MobilDevice all the data
		 *to the Government through mobileContact method.The initiator 
		 *is sent in the form of a Hash which is produced by SHA-256 
		 *and contactInfo string which sends data in the form of an XML 
		 *string. The Xml is in the form:-
		 *<contactInfo>
		 *		<testHash>
		 *			<test>cov123</test>
		 *			<test>cov234</test>
		 *		<testHash>
		 *		<contact_List>
		 *		     <contact>
		 *				<individual>rj</individual>
		 *				<Date>134</Date>
		 *				<Duration>45</Duration>
		 *           </contact>
		 *		</contact_List>
		 *</contactInfo>
		 *The anticipated error in this method can be due to error in the 
		 *obtaining of user and deviceName in the constructor or due to the
		 *error in output of mobileContact which will be handled in taht method seperately. 
		 **/
		MessageDigest md;
		
		try {
			
			
			if(flag_null_input==true) {
				
			
				return false;
			
			
			}
			
			md = MessageDigest.getInstance("SHA-256");
			
			String initiator=user+deviceName; //Concatenating user and DeviceName
			
			md.update(initiator.getBytes());
			
			byte[] digest = md.digest(); 
			
			//Converting the byte array in to HexString format
		    StringBuffer hexString = new StringBuffer();
			
		    for (int i = 0;i<digest.length;i++) {
		    
		    
		    	hexString.append(String.format("%02X", digest[i]));
		       
		    
		    }
			
		    String contactInfo = "<contactInfo>\n<testHash>\n"; //Making the Xml format
		    
			for(int i=0;i<this.testHash.size();i++) {
		    
			
				contactInfo+="<tests>"+this.testHash.get(i)+"</tests>\n";//ContactInfo saving test hashes
		       
			
			}
		    
			contactInfo+="</testHash>\n<contact_List>\n";
		    
			for(int i=0;i<this.contact_list.size();i++) {
		    //Storing of date and duration
		    	contactInfo+="<contact>\n<individual>"+this.contact_list.get(i).getIndividual()
		    			   +"</individual>\n"+"<Date>"+this.contact_list.get(i).getDate()+"</Date>\n"
		    			   +"<Duration>"+this.contact_list.get(i).getDuration()+"</Duration>\n</contact>\n";
		       
		    }
		    
		    contactInfo+="</contact_List>\n</contactInfo>";
		     
			return this.contactTracer.mobileContact(hexString.toString(), contactInfo);
		
		} 
		
		catch (NoSuchAlgorithmException e) {
		
			// TODO Auto-generated catch block
			return false;
		}
		
	}
}
