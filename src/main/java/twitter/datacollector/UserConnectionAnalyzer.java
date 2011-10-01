package twitter.datacollector;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;

public class UserConnectionAnalyzer {

	public UserConnectionAnalyzer() {
	};

	public static void collectData(String screenName, int connectionDepth) {
		
		Twitter twitter = TwitterFactory.getSingleton();
		try {
			ResponseList<UserList> allUserLists = twitter.getAllUserLists(screenName);			
		//	allUserLists.get(0).getUser().
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
