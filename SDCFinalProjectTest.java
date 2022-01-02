import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.junit.Test;

public class SDCFinalProjectTest {
    
    String sqlConfig="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\sqlconfig";
	String properties="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\properties.txt";
	String Properties1="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\Properties1";
	String Properties2="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\Properties2";
	String Properties3="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\Properties3";
	String Properties4="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\Properties4";
	String Properties5="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\Properties5";
	String Properties7="C:\\\\\\\\Users\\\\\\\\admin\\\\\\\\Desktop\\\\\\\\Java\\\\\\\\SDCFinalProject\\\\\\\\src\\\\\\\\Properties7";
	@Test
	public void AllOtherExceptFindGathering() {
           
		
		Government gov =new Government(sqlConfig);
		MobileDevice A = new MobileDevice(properties, gov);
		MobileDevice B = new MobileDevice(Properties1, gov);
		MobileDevice C = new MobileDevice(Properties2, gov);
		MobileDevice D = new MobileDevice(Properties3, gov);
		MobileDevice E = new MobileDevice(Properties4, gov);
		MobileDevice F = new MobileDevice(Properties5, gov);
		MobileDevice G = new MobileDevice(Properties7, gov);
		String a_Hash=HashConvetor(properties);
		String b_Hash=HashConvetor(Properties1);
		String c_Hash=HashConvetor(Properties2);
		String d_Hash=HashConvetor(Properties3);
		String e_Hash=HashConvetor(Properties4);
		String f_Hash=HashConvetor(Properties5);
		String g_Hash=HashConvetor(Properties7);
		
		A.positiveTest("anti1");
		A.positiveTest("anti2");
		A.positiveTest("anti3");
		B.positiveTest("anti4");
		C.positiveTest("anti5");
		D.positiveTest("anti6");
		E.positiveTest("anti7");
		
		assertEquals(false,A.synchronizeData());
		gov.recordTestResult("anti1", 67, true);
		B.recordContact(a_Hash,66 ,10);
		assertEquals(true,B.synchronizeData());
		assertEquals(false,B.synchronizeData());
		C.recordContact(b_Hash,66 ,10);
		assertEquals(false,C.synchronizeData());
		gov.recordTestResult("anti4",65 ,true);
		assertEquals(true,C.synchronizeData());
	}
	
	
	
	
	
	@Test
	public void recordTestResult_Before_PositiveTest() {
           
		
		Government gov =new Government(sqlConfig);
		   MobileDevice junit=new MobileDevice(properties,gov);
		   String b_Hash=HashConvetor(Properties1);
		   gov.recordTestResult("cov1",1001 ,true);
		   MobileDevice mobileDevice2 = new MobileDevice(Properties1, gov);
		   junit.recordContact(b_Hash,1000 , 30);
		   mobileDevice2.positiveTest("cov1");
		   mobileDevice2.synchronizeData();
		   assertEquals(true,junit.synchronizeData());
		   
	}
	
	@Test
	public void PositiveTest_BeforeRecordTestResult() {
           
		
		   Government gov =new Government(sqlConfig);
		   MobileDevice junit=new MobileDevice(Properties2,gov);
		   String b_Hash=HashConvetor(Properties3);
		   
		   MobileDevice mobileDevice2 = new MobileDevice(Properties3, gov);
		   junit.recordContact(b_Hash,1001 , 30);
		   mobileDevice2.positiveTest("cov2");
		   gov.recordTestResult("cov2",1000,true);
		   mobileDevice2.synchronizeData();
		   assertEquals(true,junit.synchronizeData());
		   
	}
	
	
	@Test
	public void syncDataReportsNewContactsOnly() {
           		
	           Government gov =new Government(sqlConfig);
		   MobileDevice junit=new MobileDevice(Properties2,gov);
		   String b_Hash=HashConvetor(Properties3);  
		   MobileDevice mobileDevice2 = new MobileDevice(Properties3, gov);
		   String c_Hash=HashConvetor(Properties4);  
		   MobileDevice mobileDevice3 = new MobileDevice(Properties4, gov);
		   mobileDevice2.positiveTest("cov5");
		   gov.recordTestResult("cov5",180 ,true);
		   junit.recordContact(b_Hash,184, 30);
		   assertEquals(false,junit.synchronizeData());
		   assertEquals(false,mobileDevice2.synchronizeData());
		   assertEquals(true,junit.synchronizeData());
		   assertEquals(false,junit.synchronizeData());
		   mobileDevice3.positiveTest("cov6");
		   gov.recordTestResult("cov6",180 ,true);
		   junit.recordContact(c_Hash,185, 30);
		   mobileDevice3.synchronizeData();
		   assertEquals(true,junit.synchronizeData());
		
	}
	@Test
	public void find_dataFlow() {
           
		Government gov =new Government(sqlConfig);
		
		int x = gov.findGatherings(670,3, 10,0.5f);
		assertEquals(0,x);
		
		MobileDevice mobileDevice1 = new MobileDevice(properties, gov);
		MobileDevice mobileDevice2 = new MobileDevice(Properties1, gov);
		MobileDevice mobileDevice3 = new MobileDevice(Properties2, gov);
		MobileDevice mobileDevice4 = new MobileDevice(Properties3, gov);
		MobileDevice mobileDevice5 = new MobileDevice(Properties4, gov);		
		
		String a_Hash=HashConvetor(properties);
		String b_Hash=HashConvetor(Properties1);
		String c_Hash=HashConvetor(Properties2);
		String d_Hash=HashConvetor(Properties3);
		String e_Hash=HashConvetor(Properties4);
		
		mobileDevice1.recordContact(b_Hash,670,9);
		mobileDevice1.recordContact(b_Hash,670,2);
		mobileDevice1.recordContact(c_Hash,670,11);
		mobileDevice1.recordContact(d_Hash,670,11);
		
		mobileDevice2.recordContact(a_Hash,670,9);
		mobileDevice2.recordContact(a_Hash,670,2);
		mobileDevice2.recordContact(c_Hash,670,11);
		mobileDevice2.recordContact(d_Hash,670,11);
		mobileDevice2.recordContact(e_Hash,670,11);
		
		mobileDevice3.recordContact(a_Hash,670,11);
		mobileDevice3.recordContact(b_Hash,670,11);
		
		mobileDevice4.recordContact(a_Hash,670,11);
		mobileDevice4.recordContact(b_Hash,670,11);
		mobileDevice4.recordContact(e_Hash,670,11);
		
		mobileDevice5.recordContact(d_Hash,670,11);
		mobileDevice5.recordContact(b_Hash,670,11);
		mobileDevice1.synchronizeData();
		mobileDevice2.synchronizeData();
		mobileDevice3.synchronizeData();
		mobileDevice4.synchronizeData();
		mobileDevice5.synchronizeData();
		int y = gov.findGatherings(670,4, 10,0.5f);
		
		assertEquals(2,y);
		
		
			
	}
	
	
	
	
	private String HashConvetor(String configFile) {
		// TODO Auto-generated method stub
		String user = null;
		String deviceName = null;
		Properties prop = new Properties();
        try {
      	  
      	  FileInputStream fis = new FileInputStream(configFile);
      	// load from input stream
            prop.load(fis);
            user=prop.getProperty("user");
            deviceName=prop.getProperty("deviceName");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
			String initiator=user+deviceName;
			md.update(initiator.getBytes());
			byte[] digest = md.digest(); 
			//Converting the byte array in to HexString format
		      StringBuffer hexString = new StringBuffer();
		      for (int i = 0;i<digest.length;i++) {
		          hexString.append(String.format("%02X", digest[i]));
            
		      }
		      
		      return hexString.toString();
		      
		      }
        catch(FileNotFoundException e) {
        	
      	  System.out.println(e.getMessage());
        
        } 
        catch (IOException e) {
			// TODO Auto-generated catch block
        	System.out.println(e.getMessage());
		}
        catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
        	System.out.println(e.getMessage());
		}	
        
        

		return null;
	}

}
