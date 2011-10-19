/**
 * 
 */
package twitter.utils;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import dbutils.HibernateUtil;

import twitter.dto.StatusDto;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * @author pulkit and sapan
 * 
 */
public class StatusStreamListener implements StatusListener {

	static int BATCH_SIZE = 1;
	static int COMMIT_SIZE = 10;
	private static Session session = HibernateUtil.getSessionFactory()
			.getCurrentSession();
	private static Transaction transaction = session.beginTransaction();
	static int countTweets = 0;
	
	private int type;

	/*
	 * @see
	 * twitter4j.StatusListener#onDeletionNotice(twitter4j.StatusDeletionNotice)
	 */
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see twitter4j.StatusListener#onScrubGeo(long, long)
	 */
	public void onScrubGeo(long userId, long upToStatusId) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see twitter4j.StatusListener#onStatus(twitter4j.Status)
	 */
	public void onStatus(Status status) {
		countTweets++;
		StatusDto statusDto = new StatusDto(status);
		statusDto.setType(type);
		try {
			session.saveOrUpdate(statusDto);
		} catch (NonUniqueObjectException e) {
			StatusDto existingStatus = (StatusDto) session.get(StatusDto.class, statusDto.getId());
			if (existingStatus != null) {
				statusDto.setType(existingStatus.getType()|this.type);
			}
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

	/*
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see twitter4j.StreamListener#onException(java.lang.Exception)
	 */
	public void onException(Exception ex) {
		// TODO Auto-generated method stub

	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
