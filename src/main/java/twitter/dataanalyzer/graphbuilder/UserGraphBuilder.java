/**
 * 
 */
package twitter.dataanalyzer.graphbuilder;

import google.utils.GAddress;
import google.utils.GCoder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import twitter.dataanalyzer.utils.TwitterMatrixUtils;
import twitter.dataanalyzer.utils.TwitterFileUtils;
import twitter.dto.StatusDto;
import twitter.dto.UserDto;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import dbutils.HibernateUtil;

/**
 * @author pulkit
 * 
 */
public class UserGraphBuilder {

	public static enum LocationLevel {
		STREET, CITY, STATE, COUNTRY, CONTINENT
	}

	private static String userDescriptionSimilarityPath = "tmp/userDescriptionSimliarity.txt";

	private static String userMentionGraphPath = "tmp/userMentionGraph.txt";
	private static String userReplyGraphPath = "tmp/userReplyGraph.txt";
	private static String userDescriptionGraphPath = "tmp/userDescriptionSimliarityGraph.txt";
	private static String userPlaceSimilarityGraphPath = "tmp/userPlaceSimilarityGraph.txt";

	private static String userMentionSparseGraphPath = "tmp/userMentionSparseGraph.txt";
	private static String userReplySparseGraphPath = "tmp/userReplySparseGraph.txt";
	private static String userDescriptionSparseGraphPath = "tmp/userDescriptionSimliaritySparseGraph.txt";
	private static String userPlaceSimilaritySparseGraphPath = "tmp/userPlaceSimilaritySparseGraph.txt";

	private static double descriptionSimliaritythreshold = 0.1;
	private static LocationLevel locationSimilarityThreshold = LocationLevel.CITY;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<String> screenNames = TwitterMatrixUtils.getUserList();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();

		List<UserDto> users = new ArrayList<UserDto>();

		for (String screenName : screenNames) {
			Criteria c = session.createCriteria(UserDto.class);
			c.add(Restrictions.eq("screenName", screenName));
			UserDto u = (UserDto) c.uniqueResult();
			users.add(u);
		}

		List<Long> userIds = new ArrayList<Long>();
		for (UserDto u : users) {
			userIds.add(u.getId());
		}

		Criteria c1 = session.createCriteria(StatusDto.class);
		c1.add(Restrictions.in("user", users));
		List<StatusDto> statuses = c1.list();

		int[][] userMentionGraph = getUserMentionGraph(statuses, userIds);
		int[][] userReplyGraph = getUserReplyGraph(statuses, userIds);
		double[][] userDescriptionSimilarity = findDescriptionSimliarity(users);
		boolean[][] userDescriptionGraph = TwitterMatrixUtils.toGraph(userDescriptionSimilarity,
				descriptionSimliaritythreshold);
		boolean[][] placeSimilarityGraph = findPlaceSimilarity(users, locationSimilarityThreshold);

		TwitterFileUtils.writeSparse(userMentionGraph, userMentionSparseGraphPath);
		TwitterFileUtils.writeSparse(userReplyGraph, userReplySparseGraphPath);
		TwitterFileUtils.writeSparse(userDescriptionGraph, userDescriptionSparseGraphPath);
		TwitterFileUtils.writeSparse(placeSimilarityGraph, userPlaceSimilaritySparseGraphPath);

		TwitterFileUtils.write(userMentionGraph, userMentionGraphPath);
		TwitterFileUtils.write(userReplyGraph, userReplyGraphPath);
		TwitterFileUtils.write(userDescriptionGraph, userDescriptionGraphPath);
		TwitterFileUtils.write(placeSimilarityGraph, userPlaceSimilarityGraphPath);

		TwitterFileUtils.write(userDescriptionSimilarity, userDescriptionSimilarityPath);

	}

	private static boolean[][] findPlaceSimilarity(List<UserDto> users, LocationLevel locationSimilarityThreshold)
			throws Exception {

		String[] locations = new String[users.size()];
		boolean[][] userLocationGraph = new boolean[users.size()][users.size()];

		int userIndex = 0;
		for (UserDto u : users) {
			if (u.getLocation() == null) {
				continue;
			}

			GAddress address = GCoder.geocode(u.getLocation());
			if (address != null) {

				switch (locationSimilarityThreshold) {
				case STREET:
					locations[userIndex] = address.address;
					break;
				case CITY:
					locations[userIndex] = address.city;
					break;
				case STATE:
					locations[userIndex] = address.state;
					break;
				case COUNTRY:
					locations[userIndex] = address.countryCode;
					break;
				default:
					break;
				}
			}
			userIndex++;
		}

		BufferedWriter out = new BufferedWriter(new FileWriter("tmp/userLocations.txt"));
		for (int i = 0; i < users.size(); ++i) {
			out.write(users.get(i).getScreenName() + ":" + users.get(i).getLocation() + ":" + locations[i] + "\n");
		}
		out.close();

		for (int i = 0; i < users.size(); ++i) {
			for (int j = 0; j < users.size(); ++j) {
				if (locations[i] != null && !locations[i].isEmpty() && locations[i] == locations[j]) {
					userLocationGraph[i][j] = true;
				} else {
					userLocationGraph[i][j] = false;
				}
			}
		}

		return userLocationGraph;
	}

	public static int[][] getUserMentionGraph(List<StatusDto> statuses, List<Long> userIds) {
		int[][] userMentionConnections = new int[userIds.size()][userIds.size()];

		for (StatusDto s : statuses) {
			List<Long> mentionIds = s.getUserMentionIds();
			for (Long userId : mentionIds) {
				int mentionedIndex = userIds.indexOf(userId);
				int posterIndex = userIds.indexOf(s.getUser().getId());
				if (mentionedIndex != -1 && posterIndex != -1) {
					userMentionConnections[posterIndex][mentionedIndex] += 1;
				}
			}
		}
		return userMentionConnections;

	}

	public static int[][] getUserReplyGraph(List<StatusDto> statuses, List<Long> userIds) {
		int[][] userReplyConnections = new int[userIds.size()][userIds.size()];

		for (StatusDto s : statuses) {
			Long inReplyToUserId = s.getInReplyToUserId();
			if (inReplyToUserId == null) {
				continue;
			}
			int repliedIndex = userIds.indexOf(inReplyToUserId);
			int posterIndex = userIds.indexOf(s.getUser().getId());
			if (repliedIndex != -1 && posterIndex != -1) {
				userReplyConnections[posterIndex][repliedIndex] += 1;
			}
		}
		return userReplyConnections;
	}

	public static double[][] findDescriptionSimliarity(List<UserDto> users) {
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);

		double[][] descriptionSimilarity = new double[users.size()][users.size()];

		for (UserDto u : users) {
			if (u.getDescription() != null) {
				tfIdf.handle(u.getDescription());
			}
		}

		for (int i = 0; i < users.size(); ++i) {
			if (users.get(i).getDescription() == null)
				continue;
			for (int j = 0; j < users.size(); ++j) {
				if (users.get(j).getDescription() == null)
					continue;
				descriptionSimilarity[i][j] = tfIdf.proximity(users.get(i).getDescription(), users.get(j)
						.getDescription());
			}
		}

		return descriptionSimilarity;
	}

}
