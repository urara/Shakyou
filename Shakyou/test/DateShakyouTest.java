import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Date;

public class DateShakyouTest {

	@Test
	public void constractorTest(){
		//ˆø”–³‚µ
		assertEquals( new Date().toString(),new DateShakyou().toString());
	}
}
