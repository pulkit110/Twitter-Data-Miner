package twitter.dataanalyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import twitter.dto.FollowerIdDto;
import twitter.dto.FriendIdDto;
import twitter.dto.UserDto;
import twitter4j.conf.ConfigurationBuilder;
import dbutils.HibernateUtil;

public class AdjacencyMatrixBuilder {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthAccessToken(
				"393631018-cgeZ7fHU3EThy5ECq8MiGvNiP80BzLf6PMJk5DRT")
				.setOAuthAccessTokenSecret(
						"w5ByrXSNm0QGTV9UHw7gZuaJU3ZWMrHhgwswxssiA")
				.setOAuthConsumerKey("YZLTCc6TbAEM9g63dr56Nw")
				.setOAuthConsumerSecret(
						"BxzTFYqF3yOg10V5nY7pBo8SdFor3YUevocJnGcrkE")
				.setUseSSL(true).setDebugEnabled(true);

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		constructMatrix(session, transaction, UserDto.class);
		// @SuppressWarnings("unused")
		//
		//
		// Query q = session.createQuery("from UserDto");
		// @SuppressWarnings("unchecked")
		// List<UserDto> users = q.list();
		//
		// TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
		// .getInstance();
		//
		// TwitterStreamUtils.follow(twitterStream, users);

	}

	private static void constructMatrix(Session session,
			Transaction transaction, Class<UserDto> tableClass)
			throws IOException {
		int rowsCount = (int) AdjacencyMatrixBuilder.countRows(session,
				UserDto.class);
		boolean[][] userRelations = new boolean[rowsCount][rowsCount];
		Map<Long, Integer> m = new HashMap<Long, Integer>();

		Integer i = 0;
		List<UserDto> users = session.createQuery("from UserDto").list();

		FileWriter fw = new FileWriter("AdjacencyList.csv");
		BufferedWriter out = new BufferedWriter(fw);

		for (UserDto u : users) {
			m.put(u.getId(), i);
			i++;
		}

		for (UserDto u : users) {
			Set<FollowerIdDto> followersIds = u.getFollowersIds();
			Set<FriendIdDto> friendsIds = u.getFriendsIds();

			for (FollowerIdDto followerId : followersIds) {
				if (m.containsKey(followerId.getId())) {
					userRelations[m.get(followerId.getId())][m.get(u.getId())] = true;
				}

			}

			for (FriendIdDto friendId : friendsIds) {
				if (m.containsKey(friendId.getId())) {
					userRelations[m.get(u.getId())][m.get(friendId.getId())] = true;
				}
			}
		}

		for (int k = 0; k < userRelations.length; ++k) {
			out.write(users.get(k).getScreenName());
			for (int j = 0; j < userRelations[k].length; ++j) {
				if (userRelations[k][j]) {
					out.write(";" + users.get(j).getScreenName());
				}
			}
			out.write("\n");
		}

		// try {
		// FileWriter fw = new FileWriter("userRelations.txt");
		// BufferedWriter out = new BufferedWriter(fw);
		// for (int k = 0; k < userRelations.length; ++k) {
		// for (int j = 0; j < userRelations[k].length; ++j) {
		// if (userRelations[k][j]) {
		// out.write(1 + " ");
		// } else {
		// out.write(0 + " ");
		// }
		//
		// }
		// out.write("\n");
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public static long countRows(Session session, Class<UserDto> tableClass) {
		return ((Number) session.createCriteria(tableClass)
				.setProjection(Projections.rowCount()).uniqueResult())
				.longValue();
	}
}
