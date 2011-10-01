package twitter.datacollector;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import dbutils.HibernateUtil;

import twitter.dto.UserDto;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class UserConnectionAnalyzer {

	private static final int BATCH_SIZE = 30;

	public UserConnectionAnalyzer() {
	};

	public void collectData(String screenName, int connectionDepth) {
		
		Twitter twitter = TwitterFactory.getSingleton();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		
		try {
			long userId = twitter.showUser(screenName).getId();
			long cursor = -1;
			int countUsers = 0;
			IDs followersIds;
			do {
				followersIds = twitter.getFollowersIDs(screenName, cursor);
				long[] ids = followersIds.getIDs();
				for (long id : ids) {				
					User u = twitter.showUser(id);
					UserDto user = new UserDto(u);
					countUsers ++;
					try {
						session.saveOrUpdate(user);
					} catch (NonUniqueObjectException e) {
						session.merge(user);
					}
					
					// Save 1 round of tweets to the database
					if (countUsers == BATCH_SIZE) {
						countUsers = 0;
						session.flush();
						session.clear();
					}
				}
				cursor = followersIds.getNextCursor();
			}while (followersIds.hasNext());

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		UserConnectionAnalyzer uca = new UserConnectionAnalyzer();
		uca.collectData("pulkit110", 1);
	}
}
