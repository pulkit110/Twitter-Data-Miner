package twitter.datacollector;

import twitter.utils.TwitterStreamUtils;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetLocationCollector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(
				Twitter4jProperties.CONSUMER_KEY).setOAuthConsumerSecret(
				Twitter4jProperties.CONSUMER_SECRET).setOAuthAccessToken(
				Twitter4jProperties.ACCESS_TOKEN).setOAuthAccessTokenSecret(
				Twitter4jProperties.ACCESS_TOKEN_SECRET).setUseSSL(true)
				.setDebugEnabled(true);

		double[][] locationCoordinates = { { -122.75, 36.8 },
				{ -121.75, 37.8 }, { -74, 40 }, { -73, 41 } };

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		TwitterStreamUtils.filterByLocation(twitterStream, locationCoordinates);
	}

}
