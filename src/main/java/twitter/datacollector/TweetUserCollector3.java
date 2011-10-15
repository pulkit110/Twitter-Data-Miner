package twitter.datacollector;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import twitter.dto.UserDto;
import twitter.utils.TwitterStreamUtils;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import dbutils.HibernateUtil;

public class TweetUserCollector3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthAccessToken("391331021-vQLkjySHbKXExZq3Uw4YKib7aLaUOYFul1UmkoyA").
		setOAuthAccessTokenSecret("H1gF1IIpND0nI5MO6HQ7oSyJckvahqdZOIe8PLB1s7Y").
		setOAuthConsumerKey("NlLnAEFqGEmGIhgsb63GsA").
		setOAuthConsumerSecret("8EkSngwzOqefzukurJ8XGCbTnBZNekJ8uAK2xUhSs").setUseSSL(true).setDebugEnabled(true);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		@SuppressWarnings("unusedddddd")
		Transaction transaction = session.beginTransaction();

		Query q = session.createQuery("from UserDto");
		@SuppressWarnings("unchecked")
		List<UserDto> users = q.list();
		
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
		TwitterStreamUtils.follow(twitterStream, users);
	}

}
