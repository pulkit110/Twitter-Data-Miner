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
		// int rowsCount = (int) AdjacencyMatrixBuilder.countRows(session,
		// UserDto.class);

		Map<Long, Integer> m = new HashMap<Long, Integer>();

		Integer i = 0;
		List<UserDto> users = session.createQuery("from UserDto where connectionDepth>0").list();

		boolean[][] userRelations = new boolean[users.size()][users.size()];

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

		// for (int k = 0; k < userRelations.length; ++k) {
		// out.write(users.get(k).getScreenName());
		// for (int j = 0; j < userRelations[k].length; ++j) {
		// if (userRelations[k][j]) {
		// out.write(";" + users.get(j).getScreenName());
		// }
		// }
		// out.write("\n");
		// }

		try {
			FileWriter fw1 = new FileWriter("userRelations.csv");
			FileWriter fw2 = new FileWriter("userList.txt");
			BufferedWriter out1 = new BufferedWriter(fw1);
			BufferedWriter out2 = new BufferedWriter(fw2);
			for (int k = 0; k < userRelations.length; ++k) {
				// out2.write(users.get(k).getScreenName() + "\n");
				System.out.println(k);
				for (int j = 0; j < userRelations[k].length; ++j) {
				//	System.out.println(j);
					if (userRelations[k][j]) {
						// out1.write(1 + " ");
						out1.write(k + " " + j + "\n");
						System.out.println(k + " " + j + "\n");

					} else {
						// out1.write(0 + " ");
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error!!!");
			e.printStackTrace();
		}

	}

	public static long countRows(Session session, Class<UserDto> tableClass) {
		return ((Number) session.createCriteria(tableClass)
				.setProjection(Projections.rowCount()).uniqueResult())
				.longValue();
	}
}
