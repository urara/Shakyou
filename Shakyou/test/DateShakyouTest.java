import org.junit.Test;

import com.shakyou.Date;

import static org.junit.Assert.*;
//import java.util.Date;

public class DateShakyouTest {

	@Test
	public void constractorTest(){
		//���������ł���Ă݂�
		assertEquals( new Date().toString(),new java.util.Date().toString());
		
		//�����A���ł���Ă݂�
		//���ɂ��܂�
		//assertEquals( new Date(2011,1,1).toString(),new DateShakyou(2011,1,1).toString());
		
		
	}

	@Test
	public void methodTest(){
		assertEquals("" , "");
	}
	
	
}
