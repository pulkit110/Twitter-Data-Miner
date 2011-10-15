package twitter.datacollector;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Sapan & Pulkit
 * 
 */
public class App {
	static Session session;
	static Transaction transaction; 

	public static void main(String[] args) {

		// sample() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		// twitterStream.sample();
	}

}