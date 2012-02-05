import org.junit.Test;

import com.shakyou.Date;

import static org.junit.Assert.*;
//import java.util.Date;

public class DateShakyouTest {

	
	java.util.Date dateOrg = new java.util.Date();
	Date date = new Date();
	
	@Test
	public void constractorTest(){
		
		//tostring
		assertEquals( date.toString(), dateOrg.toString());

		dateOrg = new java.util.Date(10000L);
		date = new Date(10000L);

		//tostring
		assertEquals( date.toString(), dateOrg.toString());

		dateOrg = new java.util.Date(2012 , 02 , 04 , 10, 10, 10);
		date = new Date(2012 , 02 , 04 , 10, 10, 10);

		//tostring
		assertEquals( date.toString(), dateOrg.toString());
		
		
		
	}

	@Test
	public void publicMethodTest(){
		assertEquals(date.clone().toString() , date.toString());
		assertEquals(date.getYear(), dateOrg.getYear());
		assertEquals(date.getTimezoneOffset(), dateOrg.getTimezoneOffset());
		
		assertEquals(date.compareTo(new Date(10000L)) , 1);
		assertEquals((new Date(10000L)).compareTo(date) , -1);
		
		//System.out.println("locale = " + date.toLocaleString().toString());
	}
	
	
	@Test
	public void staticMethodTest(){
		assertEquals(Date.UTC(2012, 2, 4, 13, 13, 13),java.util.Date.UTC(2012, 2, 4, 13, 13, 13)); 
	}
	
	
}
