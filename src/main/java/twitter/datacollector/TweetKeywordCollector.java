package twitter.datacollector;

import twitter.utils.TwitterStreamUtils;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetKeywordCollector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthAccessToken("386662088-HaZiHauqf2Sa28CrlPvhyPIvjvF9K8Gt1D4JNgeK").
		setOAuthAccessTokenSecret("0NPVDRwcC345XGoo5xJLpOvyq9ZjTe9Tu3ni2ZJow").
		setOAuthConsumerKey("xJMkGGtoWyuH2XolcVp7Zw").
		setOAuthConsumerSecret("qJYOEhGpD8ccHSJ8ERAxwoZtUnucVstyTRCIH1Kio").setUseSSL(true).setDebugEnabled(true);
		
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		String[] keywords = {
				"apple"
		};
		TwitterStreamUtils.filterByKeywords(twitterStream, keywords);
	}

}
