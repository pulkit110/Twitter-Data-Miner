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

		double[][] locationCoordinates = { {-0.3475,51.3695},{0.0915,51.6435},
			{ -74.11,40.633} ,{-73.89,40.800334 },
			{ 2.241,48.784},{2.460666,48.929334 },
			{-122.529,37.6925},{-122.3094,37.8661 },
			{72.55,18.875},{73.15,19.275 }};

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		TwitterStreamUtils.filterByLocation(twitterStream, locationCoordinates);
	}

}
