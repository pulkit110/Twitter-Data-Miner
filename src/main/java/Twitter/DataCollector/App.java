package Twitter.DataCollector;

import org.hibernate.Session;

import Twitter.DTO.StatusDto;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import DBUtils.HibernateUtil;

/**
 * @author sapan & pulkit
 *
 */
public class App 
{
    public static void main(String[] args) {
    	
    	StatusListener listener = new StatusListener(){
            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
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
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        
        StatusDto status = new StatusDto();
        status.setFavorited(false);
        status.setId(1);
        status.setInReplyToScreenName("sdf");
        status.setInReplyToStatusId(213412312);
        status.setInReplyToUserId(21312);
        status.setPlace("fdsfd");
        status.setRetweetCount(7);
        status.setSource("dfaf");
        status.setText("sdaafdsaf");
        status.setUser("fdasfds");
        
        session.saveOrUpdate(status);

        session.getTransaction().commit();
        HibernateUtil.getSessionFactory().close();        
        
     // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
//        twitterStream.sample();
    }
}
