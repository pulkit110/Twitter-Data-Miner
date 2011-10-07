package twitter.datacollector;

import java.util.List;

import javax.sound.midi.Track;

import org.hibernate.Criteria;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import dbutils.HibernateUtil;

import twitter.dto.StatusDto;
import twitter.dto.UserDto;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author sapan & pulkit
 * 
 */
public class App {
	static int BATCH_SIZE = 1;
	static int COMMIT_SIZE = 10;
	static Session session;
	static Transaction transaction;

	static StatusListener listener = new StatusListener() {
		int countTweets = 0; // Count to implement batch processing

		public void onStatus(Status status) {
			countTweets++;
			StatusDto statusDto = new StatusDto(status);
			try {
				session.saveOrUpdate(statusDto);
			} catch (NonUniqueObjectException e) {
				session.merge(statusDto);
			}

			// Save 1 round of tweets to the database
			if (countTweets == BATCH_SIZE) {
				countTweets = 0;
				session.flush();
				session.clear();
				transaction.commit();
				session = HibernateUtil.getSessionFactory()
						.getCurrentSession();
				transaction = session.beginTransaction();
			}
		}

		public void onDeletionNotice(
				StatusDeletionNotice statusDeletionNotice) {
		}

		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		}

		public void onException(Exception ex) {
			ex.printStackTrace();
		}

		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub
		}
	};
	
	public static void main(String[] args) {

	
		location();

		// ConfigurationBuilder cb = new ConfigurationBuilder();
		// cb.setDebugEnabled(true).setOAuthConsumerKey(
		// Twitter4jProperties.CONSUMER_KEY).setOAuthConsumerSecret(
		// Twitter4jProperties.CONSUMER_SECRET).setOAuthAccessToken(
		// Twitter4jProperties.ACCESS_TOKEN).setOAuthAccessTokenSecret(
		// Twitter4jProperties.ACCESS_TOKEN_SECRET).setUseSSL(true).setDebugEnabled(true);

		// sample() method internally creates a thread which manipulates
		// TwitterStream and calls these adequate listener methods continuously.
		// twitterStream.sample();
	}

	void follow() {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();

		Query q = session.createQuery("from UserDto");
		@SuppressWarnings("unchecked")
		List<UserDto> users = q.list();
		long[] follow = new long[users.size()];
		int i = 0;
		for (UserDto u : users) {
			follow[i] = (long) u.getId();
			i++;
		}

		twitterStream.filter(new FilterQuery(follow));
	}
	
	static void track() {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();
		String[] keywords = {
				"apple"
		};
		twitterStream.filter(new FilterQuery().track(keywords));
	}
	
	static void location() {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(listener);
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();
		double[][] keywords = {{-122.75,36.8},{-121.75,37.8},{-74,40},{-73,41
			
		}};
		twitterStream.filter(new FilterQuery().locations(keywords));
	}
	
}
