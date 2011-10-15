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

public class TweetUserCollector1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthAccessToken("391326573-JEAj4jSUUBcNtgE2VeQR0bvnbHTgLsoO8alnhwRL").
		setOAuthAccessTokenSecret("hlwBhwgLQYxP6hY6J71F6DDILRM0hMzwK0PdU2LZY").
		setOAuthConsumerKey("59B1Fk752NQMJXK6habqKA").
		setOAuthConsumerSecret("vLaEaI9zUeyOwPs51tngBeIv63N1alo7Ngc1ErKzUE").setUseSSL(true).setDebugEnabled(true);
		
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
