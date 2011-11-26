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

import twitter.dataanalyzer.utils.LuceneIndexer;
import twitter.dataanalyzer.utils.TermDocumentUtils;
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

		int nUsers = 10;

		TweetsSimilarityFinder tweetsSimilarityFinder = new TweetsSimilarityFinder();

		tweetsSimilarityFinder.buildIndex();

		List<File> userTweets = tweetsSimilarityFinder
				.generateTweetFiles(nUsers);
		tweetsSimilarityFinder.getLuceneIndexer().addAll(
				tweetsSimilarityFinder.getDocumentDir());

		IndexReader indexReader = tweetsSimilarityFinder.readIndex();

		TermDocumentUtils.printTermFrequenciesToFile(indexReader);

	}

	private SvdMatrix createSvdMatrix(IndexReader indexReader)
			throws IOException {
		double[][] termDocMatrix = TermDocumentUtils
				.buildTermDocMatrix(indexReader);

		// Dimension of SVD
		int maxFactors = 2;
		double featureInit = 0.01;
		double initialLearningRate = 0.005;
		int annealingRate = 1000;
		double regularization = 0.00;
		double minImprovement = 0.0000;
		int minEpochs = 10;
		int maxEpochs = 50000;

		SvdMatrix matrix = SvdMatrix.svd(termDocMatrix, maxFactors,
				featureInit, initialLearningRate, annealingRate,
				regularization, null, minImprovement, minEpochs, maxEpochs);
		// The final argument is null, which turns off feedback from the SVD
		// solver during the solution process.

		return matrix;
	}

	private List<File> generateTweetFiles(int nUsers) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();

		Criteria c = session.createCriteria(UserDto.class);
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
