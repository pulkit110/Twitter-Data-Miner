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
		String[] keywords = { "apple", "mac", "macbook", "macbookair",
			"macbookpro", "os x", "osx", "osxlion", "ipod", "ipodshuffle",
			"ipodnano", "ipodclassic", "ipodtouch", "itunes", "iphone",
			"iphone3", "iphone3s", "iphone4", "iphone4s", "iphone5", "ios",
			"ios4", "ios5", "ipad", "ipad2", "ipad3", "manchesterunited",
			"manchester united", "manchester utd", "man united", "manutd",
		"man utd", "manu", "mufc" };
		TwitterStreamUtils.filterByKeywords(twitterStream, keywords);
	}

}
