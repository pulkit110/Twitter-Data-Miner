/**
 * 
 */
package twitter.dataanalyzer.graphbuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;

import twitter.dataanalyzer.utils.LuceneIndexer;
import twitter.dataanalyzer.utils.UserTweetsCombiner;
import twitter.dto.UserDto;
import dbutils.HibernateUtil;

/**
 * @author sapan and pulkit
 * 
 */
public class TweetsSimilarityFinder {

	String indexPath = "tmp/luceneIndex";
	String documentDir = "tmp/userTweets";

	LuceneIndexer luceneIndexer;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int nUsers = 10;

		TweetsSimilarityFinder tweetsSimilarityFinder = new TweetsSimilarityFinder();

		tweetsSimilarityFinder.buildIndex();

		List<File> userTweets = tweetsSimilarityFinder
				.generateTweetFiles(nUsers);
		tweetsSimilarityFinder.getLuceneIndexer().addAll(
				tweetsSimilarityFinder.getDocumentDir());

		IndexReader indexReader = tweetsSimilarityFinder.readIndex();

	}

	private List<File> generateTweetFiles(int nUsers) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Criteria c = session.createCriteria(UserDto.class);
		c.setMaxResults(nUsers);
		List<UserDto> users = c.list();

		UserTweetsCombiner userTweetsCombiner = new UserTweetsCombiner(
				documentDir);
		userTweetsCombiner.setUsers(users);
		try {
			return userTweetsCombiner.generateTweetFiles();
		} catch (IOException e) {
			System.out.println("Unable to generate tweet files");
			e.printStackTrace();
		}

		return null;
	}

	public void buildIndex() {
		try {
			luceneIndexer = new LuceneIndexer(indexPath);
		} catch (IOException e) {
			System.out.println("Unable to build index");
			e.printStackTrace();
		}
	}

	public IndexReader readIndex() {
		if (luceneIndexer == null) {
			return null;
		}

		IndexReader indexReader = null;
		try {
			indexReader = IndexReader.open(luceneIndexer.getIndexDir());
		} catch (CorruptIndexException e) {
			System.out.println("Index curropted");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to open index");
			e.printStackTrace();
		}

		return indexReader;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public String getDocumentDir() {
		return documentDir;
	}

	public void setDocumentDir(String documentDir) {
		this.documentDir = documentDir;
	}

	public LuceneIndexer getLuceneIndexer() {
		return luceneIndexer;
	}

	public void setLuceneIndexer(LuceneIndexer luceneIndexer) {
		this.luceneIndexer = luceneIndexer;
	}
}
