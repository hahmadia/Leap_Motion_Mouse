import com.leapmotion.leap.Controller;

/**
 * This is file includes the main method to run our LeapMouseV2
 * @author Hossein Ahmadian-Yazdi
 * @version March 12, 2016
 */

public class LeapMouseV2Main {
	public static void main(String[] args) {
		
		MyListener listener = new MyListener();
		Controller c = new Controller();
		c.addListener(listener);

		try{
			System.in.read();
		}
		catch (Exception e){}

		c.removeListener(listener);

	}
}


