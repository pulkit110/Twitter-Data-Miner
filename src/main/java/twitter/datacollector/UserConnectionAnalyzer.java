package twitter.datacollector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;

import twitter.dto.FollowerIdDto;
import twitter.dto.FriendIdDto;
import twitter.dto.UserDto;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import dbutils.HibernateUtil;

public class UserConnectionAnalyzer {

	private static final int BATCH_SIZE = 1;
	private static final int USER_REQUEST_LIMIT = 100;
	private static Session session;
	private static Transaction transaction;
	private static Twitter twitter;
	private static int countUsers;

	public static int TRACK_FOLLOWERS = 1;
	public static int TRACK_FOLLOWING = 2;
	public static int TRACK_BOTH = 3;

	public static int THRESHOLD_FOLLOWERS_COUNT = 5000;
	public static int THRESHOLD_FOLLOWING_COUNT = 1000;

	static org.slf4j.Logger logger = LoggerFactory
			.getLogger(UserConnectionAnalyzer.class);

	public UserConnectionAnalyzer() {
	};

	public void collectData(String screenName, int connectionDepth, int type) {

		if (connectionDepth <= 0) {
			return;
		}

		logger.info("Collecting followers for " + screenName);

		long cursor = -1;
		long cursor1 = -1;

		Criteria criteria = session.createCriteria(UserDto.class);
		criteria.add(Restrictions.eq("screenName", screenName));
		UserDto currentUser = (UserDto) criteria.uniqueResult();
		currentUser.setVisited(true);

		if (currentUser.getFollowersIds() == null) {
			currentUser.setFollowersIds(new HashSet<FollowerIdDto>());
		}
		if (currentUser.getFriendsIds() == null) {
			currentUser.setFriendsIds(new HashSet<FriendIdDto>());
		}

		if (type == UserConnectionAnalyzer.TRACK_FOLLOWERS) {
			collectConnectedUsers(screenName, cursor, connectionDepth, type);
		} else if (type == UserConnectionAnalyzer.TRACK_FOLLOWING) {
			collectConnectedUsers(screenName, cursor, connectionDepth, type);
		} else {
			List<UserDto> newUsers = new ArrayList<UserDto>();

			IDs followersIds = null;
			IDs friendsIds = null;
			do {
				boolean successful = false;
				while (!successful) {
					try {
						followersIds = twitter.getFollowersIDs(screenName, cursor);
						friendsIds = twitter.getFriendsIDs(screenName, cursor1);
						successful = true;
					} catch (TwitterException e) {
						successful = false;
						e.printStackTrace();
						if (!e.isCausedByNetworkIssue()) {
							//User might be protected; There is nothing we can do about it
							break;
						}
						try {
							Thread.sleep(12000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				if (!successful) {
					break;
				}

				Set<FollowerIdDto> followersSet = new HashSet<FollowerIdDto>();
				Set<FriendIdDto> friendsSet = new HashSet<FriendIdDto>();
				for (long id : followersIds.getIDs()) {
					followersSet
							.add(new FollowerIdDto(id, currentUser.getId()));
				}
				for (long id : friendsIds.getIDs()) {
					friendsSet.add(new FriendIdDto(id, currentUser.getId()));
				}
				currentUser.getFollowersIds().addAll(followersSet);
				currentUser.getFriendsIds().addAll(friendsSet);

				try {
					Thread.sleep(11000);
				} catch (InterruptedException e1) {
					logger.info(e1.getMessage());
				}
				long[] followerIds = followersIds.getIDs();
				long[] friendIds = friendsIds.getIDs();
				long[] ids = new long[followerIds.length + friendIds.length];
				for (int i = 0; i < followerIds.length; ++i) {
					ids[i] = followerIds[i];
				}
				for (int i = 0; i < friendIds.length; ++i) {
					ids[followerIds.length + i] = friendIds[i];
				}
				// break ids array into arrays of size of user request
				// limit(100)
				for (int i = 0; i <= ids.length / USER_REQUEST_LIMIT; ++i) {
					int length = (i == ids.length / USER_REQUEST_LIMIT) ? ids.length
							- i * USER_REQUEST_LIMIT
							: USER_REQUEST_LIMIT;
					long[] idsSubArray = new long[length];
					System.arraycopy(ids, i * USER_REQUEST_LIMIT, idsSubArray,
							0, length);
					ResponseList<User> users = null;
					successful = false;
					while (!successful) {
						try {
							users = twitter.lookupUsers(idsSubArray);
							successful = true;
						} catch (TwitterException e) {
							successful = false;
							e.printStackTrace();
							try {
								Thread.sleep(12000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
					
					try {
						Thread.sleep(11000);
					} catch (InterruptedException e1) {
						logger.info(e1.getMessage());
					}
					for (User u : users) {
						if (u.getFollowersCount() > THRESHOLD_FOLLOWERS_COUNT
								|| u.getFriendsCount() > THRESHOLD_FOLLOWING_COUNT) {
							continue;
						}
						UserDto user = new UserDto(u);
						user.setConnectionDepth(connectionDepth - 1);
						countUsers++;
						newUsers.add(user);

						boolean visitedIncorrectly = false;
						boolean newVisit = false;
						UserDto existingUser = (UserDto) session.get(
								UserDto.class, user.getId());
						if (existingUser == null) {
							session.saveOrUpdate(user);
							newVisit = true;
						} else {
							if (existingUser.getConnectionDepth() < user
									.getConnectionDepth()) {
								visitedIncorrectly = true;
								session.merge(user);
							}
						}

						if (countUsers == BATCH_SIZE) {
							countUsers = 0;
							session.flush();
							session.clear();
							transaction.commit();
							session = HibernateUtil.getSessionFactory()
									.getCurrentSession();
							transaction = session.beginTransaction();
						}

						// if (newVisit || visitedIncorrectly) {
						// collectData(user.getScreenName(),
						// connectionDepth - 1, type);
						// }
					}
				}
				cursor = followersIds.getNextCursor();
				cursor1 = friendsIds.getNextCursor();
			} while (friendsIds.hasNext() || followersIds.hasNext());
			session.merge(currentUser);

			for (UserDto u : newUsers) {
				collectData(u.getScreenName(), u.getConnectionDepth(), type);
			}
		}

		session.flush();
		session.clear();
	}

	public void collectConnectedUsers(String screenName, long cursor,
			int connectionDepth, int type) {
		IDs usersIds = null;
		Criteria criteria = session.createCriteria(UserDto.class);
		criteria.add(Restrictions.eq("screenName", screenName));
		UserDto currentUser = (UserDto) criteria.uniqueResult();
		currentUser.setVisited(true);
		if (currentUser.getFollowersIds() == null) {
			currentUser.setFollowersIds(new HashSet<FollowerIdDto>());
		}
		if (currentUser.getFriendsIds() == null) {
			currentUser.setFriendsIds(new HashSet<FriendIdDto>());
		}
		List<UserDto> newUsers = new ArrayList<UserDto>();

		do {
			if (type == UserConnectionAnalyzer.TRACK_FOLLOWERS) {
				boolean successful = false;
				while (!successful) {
					try {
						usersIds = twitter.getFollowersIDs(screenName, cursor);
						successful = true;
					} catch (TwitterException e) {
						successful = false;
						e.printStackTrace();
						if (!e.isCausedByNetworkIssue()) {
							//User might be protected; There is nothing we can do about it
							break;
						}
						try {
							Thread.sleep(12000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				if (!successful) {
					logger.error("Skipping user " + currentUser.getScreenName());
					break;
				}
				Set<FollowerIdDto> followersSet = new HashSet<FollowerIdDto>();

				for (long id : usersIds.getIDs()) {
					followersSet
							.add(new FollowerIdDto(id, currentUser.getId()));
				}
				currentUser.getFollowersIds().addAll(followersSet);
				try {
					Thread.sleep(11000);
				} catch (InterruptedException e1) {
					logger.info(e1.getMessage());
				}
			} else if (type == UserConnectionAnalyzer.TRACK_FOLLOWING) {
				boolean successful = false;
				while (!successful) {
					try {
						usersIds = twitter.getFriendsIDs(screenName, cursor);
						successful = true;
					} catch (TwitterException e) {
						successful = false;
						e.printStackTrace();
						
						if (!e.isCausedByNetworkIssue()) {
							//User might be protected; There is nothing we can do about it
							break;
						}
						try {
							Thread.sleep(12000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

				if (!successful) {
					logger.error("Skipping user " + currentUser.getScreenName());
					break;
				}
				
				Set<FriendIdDto> friendsSet = new HashSet<FriendIdDto>();
				for (long id : usersIds.getIDs()) {
					friendsSet.add(new FriendIdDto(id, currentUser.getId()));
				}
				currentUser.getFriendsIds().addAll(friendsSet);
				try {
					Thread.sleep(11000);
				} catch (InterruptedException e1) {
					logger.info(e1.getMessage());
				}
			} else {
				logger.error("UserConnectionAnalyzer: Unexpected type");
				return;
			}

			long[] ids = usersIds.getIDs();

			for (int i = 0; i <= ids.length / USER_REQUEST_LIMIT; ++i) {
				int length = (i == ids.length / USER_REQUEST_LIMIT) ? ids.length
						- i * USER_REQUEST_LIMIT
						: USER_REQUEST_LIMIT;
				long[] idsSubArray = new long[length];
				System.arraycopy(ids, i * USER_REQUEST_LIMIT, idsSubArray, 0,
						length);
				ResponseList<User> users = null;
				boolean successful = false;
				while (!successful) {
					try {
						users = twitter.lookupUsers(idsSubArray);
						successful = true;
					} catch (TwitterException e) {
						successful = false;
						e.printStackTrace();
						try {
							Thread.sleep(12000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

				try {
					Thread.sleep(11000);
				} catch (InterruptedException e1) {
					logger.info(e1.getMessage());
				}
				for (User u : users) {
					if (u.getFollowersCount() > THRESHOLD_FOLLOWERS_COUNT
							|| u.getFriendsCount() > THRESHOLD_FOLLOWING_COUNT) {
						continue;
					}
					UserDto user = new UserDto(u);
					user.setConnectionDepth(connectionDepth - 1);
					countUsers++;
					newUsers.add(user);
					UserDto existingUser = (UserDto) session.get(UserDto.class,
							user.getId());
					if (existingUser == null) {
						session.saveOrUpdate(user);
					} else {
						if (existingUser.getConnectionDepth() < user
								.getConnectionDepth()) {
							session.merge(user);
						}
					}
					if (countUsers == BATCH_SIZE) {
						countUsers = 0;
						session.flush();
						session.clear();
						transaction.commit();
						session = HibernateUtil.getSessionFactory()
								.getCurrentSession();
						transaction = session.beginTransaction();
					}

					// if (newVisit || visitedIncorrectly) {
					// collectData(user.getScreenName(), connectionDepth - 1,
					// type);
					// }

				}
			}
			cursor = usersIds.getNextCursor();
		} while (usersIds.hasNext());
		session.merge(currentUser);

		for (UserDto u : newUsers) {
			collectData(u.getScreenName(), u.getConnectionDepth(), type);
		}

	}

	public static void main(String[] args) {
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		transaction = session.beginTransaction();
		twitter = TwitterFactory.getSingleton();
		countUsers = 0;

		String screenName = "EPFLNews";
		UserConnectionAnalyzer uca = new UserConnectionAnalyzer();

		try {
			UserDto u = new UserDto(twitter.showUser(screenName));
			u.setConnectionDepth(2);
			u.setVisited(true);
			session.saveOrUpdate(u);
			// countUsers++;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		uca.collectData(screenName, 2, UserConnectionAnalyzer.TRACK_BOTH);
		transaction.commit();
		// session.close();
	}
}
