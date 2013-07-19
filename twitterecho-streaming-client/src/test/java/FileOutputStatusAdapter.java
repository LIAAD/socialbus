import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;


public class FileOutputStatusAdapter extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testFileNameBasedOnTime(){
		
		Calendar.getInstance(Locale.UK);
		
		int interval = 5;
		
		while(true){
			Date now = new Date();
			System.out.println(now);
			if(now.getSeconds() % 5 == 0){
				
				System.out.println(now.getSeconds());
			}
			
		}
		
		
	}
}
