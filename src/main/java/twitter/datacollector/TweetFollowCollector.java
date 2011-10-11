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

public class TweetFollowCollector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthAccessToken("376687503-1MYc8MGwAg8FCSV7SHP2L9gv1pRIyrJH3Lg8omYb").
		setOAuthAccessTokenSecret("dxL4yndr2w6yllGPv8HewsY7K4Zt82Pc6jJRkngI").
		setOAuthConsumerKey("av0yGys1EAlP11wKdCfg").
		setOAuthConsumerSecret("6CJjiVW5JD6eUQVLV7lrpNj2YVeMOscIN72kcxJdk").setUseSSL(true).setDebugEnabled(true);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();

		Query q = session.createQuery("from UserDto");
		@SuppressWarnings("unchecked")
		List<UserDto> users = q.list();
		
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
		TwitterStreamUtils.follow(twitterStream, users);
	}

}
