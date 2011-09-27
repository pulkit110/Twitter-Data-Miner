package Twitter.DataCollector;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import DBUtils.HibernateUtil;
import Twitter.DTO.StatusDto;

/**
 * @author sapan & pulkit
 *
 */
public class App 
{
	static int BATCH_SIZE = 30;
	static int COMMIT_SIZE = 10;
	static Session session;
	static Transaction transaction;
	
    public static void main(String[] args) {
    	
    	StatusListener listener = new StatusListener(){
    		int countTweets = 0;	// Count to implement batch processing
            
    		public void onStatus(Status status) {
                countTweets ++;
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
                }
            }
            
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
            
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
            
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
			}			
        };
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(Twitter4jProperties.CONSUMER_KEY)
          .setOAuthConsumerSecret(Twitter4jProperties.CONSUMER_SECRET)
          .setOAuthAccessToken(Twitter4jProperties.ACCESS_TOKEN)
          .setOAuthAccessTokenSecret(Twitter4jProperties.ACCESS_TOKEN_SECRET);
        
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(listener);
        
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        transaction = session.beginTransaction();
             
        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.sample();
    }
}
