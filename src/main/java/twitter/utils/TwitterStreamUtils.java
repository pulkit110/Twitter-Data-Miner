/**
 * 
 */
package twitter.utils;

import java.util.List;

import twitter.dto.StatusDto;
import twitter.dto.UserDto;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;

/**
 * @author Pulkit and Sapan
 * 
 */
public class TwitterStreamUtils {

	public static void follow(TwitterStream twitterStream, List<UserDto> users) {
		StatusStreamListener followStreamListener = new StatusStreamListener();
		followStreamListener.setType(StatusDto.TYPE_FOLLOW);
		twitterStream.addListener(followStreamListener);

		long[] followUserIds = new long[users.size()];
		int i = 0;
		for (UserDto u : users) {
			followUserIds[i] = (long) u.getId();
			i++;
		}

		twitterStream.filter(new FilterQuery(followUserIds));
	}

	public static void filterByKeywords(TwitterStream twitterStream,
			String[] keywords) {
		StatusStreamListener keywordStreamListener = new StatusStreamListener();
		keywordStreamListener.setType(StatusDto.TYPE_KEYWORD);
		twitterStream.addListener(keywordStreamListener);
		twitterStream.filter(new FilterQuery().track(keywords));
	}

	public static void filterByLocation(TwitterStream twitterStream,
			double[][] locationCoordinates) {
		StatusStreamListener locationStreamListener = new StatusStreamListener();
		locationStreamListener.setType(StatusDto.TYPE_LOCATION);
		twitterStream.addListener(locationStreamListener);
		twitterStream.filter(new FilterQuery().locations(locationCoordinates));
	}
}
