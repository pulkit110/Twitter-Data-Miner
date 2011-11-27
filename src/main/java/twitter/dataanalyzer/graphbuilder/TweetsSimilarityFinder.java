/**
 * 
 */
package twitter.dataanalyzer.graphbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import twitter.dataanalyzer.utils.LuceneIndexer;
import twitter.dataanalyzer.utils.TwitterMatrixUtils;
import twitter.dataanalyzer.utils.UserTweetsCombiner;
import twitter.dto.UserDto;

import com.aliasi.matrix.SvdMatrix;

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
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void main(String[] args) throws CorruptIndexException,
			IOException {

		int nUsers = 400;

		TweetsSimilarityFinder tweetsSimilarityFinder = new TweetsSimilarityFinder();

		tweetsSimilarityFinder.buildIndex();

		List<File> userTweets = tweetsSimilarityFinder
				.generateTweetFiles(nUsers);
		tweetsSimilarityFinder.getLuceneIndexer().addAll(
				tweetsSimilarityFinder.getDocumentDir());

		IndexReader indexReader = tweetsSimilarityFinder.readIndex();

		TwitterMatrixUtils.printTermFrequenciesToFile(indexReader);
		
		double[][] termDocMatrix = TwitterMatrixUtils
				.buildTermDocMatrix(indexReader);
		
		TwitterMatrixUtils.docsCosineSimilarity(termDocMatrix);
		TwitterMatrixUtils.docsLSASimilarity(termDocMatrix);

	}

	private List<File> generateTweetFiles(int nUsers) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();

		Criteria c = session.createCriteria(UserDto.class);
		c.add(Restrictions.eq("visited", true));
		c.add(Restrictions.gt("connectionDepth", 0));
//		c.add(Restrictions.gt("followersCount", 25));
//		c.add(Restrictions.gt("friendsCount", 25));
//		c.add(Restrictions.gt("statusesCount", 50));
		c.setMaxResults(nUsers);
		List<UserDto> users = c.list();

		session.close();

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
