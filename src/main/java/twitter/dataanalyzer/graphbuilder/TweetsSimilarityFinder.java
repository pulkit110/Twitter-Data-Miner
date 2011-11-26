/**
 * 
 */
package twitter.dataanalyzer.graphbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
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
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException {

		int nUsers = 10;

		TweetsSimilarityFinder tweetsSimilarityFinder = new TweetsSimilarityFinder();

		tweetsSimilarityFinder.buildIndex();

		List<File> userTweets = tweetsSimilarityFinder
				.generateTweetFiles(nUsers);
		tweetsSimilarityFinder.getLuceneIndexer().addAll(
				tweetsSimilarityFinder.getDocumentDir());

		IndexReader indexReader = tweetsSimilarityFinder.readIndex();
		List<TermFreqVector> TfMatrix = new ArrayList<TermFreqVector>();
		
		int numDocs = indexReader.numDocs();
		List<String> docsList = new ArrayList<String>();
		
		FileWriter fw2 = new FileWriter("tmp/docs.txt");
		BufferedWriter out2 = new BufferedWriter(fw2);
		
		for (int i = 0; i < numDocs; ++i) {
			Document d = indexReader.document(i);
			out2.write(d.getField("path").stringValue() + "\n");
			TermFreqVector termFreqVector = indexReader.getTermFreqVector(i, "contents");
			List<Fieldable> fileds = d.getFields();
			TfMatrix.add(termFreqVector);
		}
		out2.close();
		
		TermEnum terms = indexReader.terms();
		List<String> termsList = new ArrayList<String>();
		
		FileWriter fw = new FileWriter("tmp/termdoc.txt");
		BufferedWriter out = new BufferedWriter(fw);
		
		FileWriter fw1 = new FileWriter("tmp/terms.txt");
		BufferedWriter out1 = new BufferedWriter(fw1);


		
		int termIndex = 0;
		while (terms.next()) {
			termsList.add(terms.term().toString());
			out1.write(terms.term().text() + "\n");
			TermDocs termDocs = indexReader.termDocs(terms.term());
			while (termDocs.next()) {
				out.write(termIndex + " " + termDocs.doc() + " " + termDocs.freq() + "\n");
			}
			++ termIndex;
		}
		out.close();
		out1.close();

		
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
