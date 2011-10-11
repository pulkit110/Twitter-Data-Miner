/**
 * 
 */
package twitter.utils;

import java.util.List;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import twitter.dto.StatusDto;
import twitter.dto.UserDto;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import dbutils.HibernateUtil;

/**
 * @author Pulkit and Sapan
 * 
 */
public class TwitterStreamUtils {

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
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				transaction = session.beginTransaction();
			}
		}

		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
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

	public static void follow(TwitterStream twitterStream, List<UserDto> users) {
		twitterStream.addListener(listener);
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();

		long[] follow = new long[users.size()];
		int i = 0;
		for (UserDto u : users) {
			follow[i] = (long) u.getId();
			i++;
		}

		twitterStream.filter(new FilterQuery(follow));
	}

	public static void filterByKeywords(TwitterStream twitterStream,
			String[] keywords) {
		twitterStream.addListener(listener);
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();
		twitterStream.filter(new FilterQuery().track(keywords));
	}

	public static void filterByLocation(TwitterStream twitterStream,
			double[][] locationCoordinates) {
		twitterStream.addListener(listener);
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();
		twitterStream.filter(new FilterQuery().locations(locationCoordinates));
	}
}
