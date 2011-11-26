package twitter.dataanalyzer.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;

/**
 * 
 * @author pulkit and sapan
 *
 */
public class TermDocumentUtils {
	
	static String docsListPath = "tmp/docs.txt";
	static String termDocMatrixPath = "tmp/termdoc.txt";
	static String termsListPath = "tmp/terms.txt";
	
	public static void printTermFrequenciesToFile(IndexReader indexReader) throws IOException {

		int numDocs = indexReader.numDocs();

		// Write all document titles to file
		FileWriter fw2 = new FileWriter(docsListPath);
		BufferedWriter out2 = new BufferedWriter(fw2);
		for (int i = 0; i < numDocs; ++i) {
			Document d = indexReader.document(i);
			out2.write(d.getField("path").stringValue() + "\n");
		}
		out2.close();

		TermEnum terms = indexReader.terms();
		
		// Write term document matrix and terms to files
		FileWriter fw = new FileWriter(termDocMatrixPath);
		BufferedWriter out = new BufferedWriter(fw);
		FileWriter fw1 = new FileWriter(termsListPath);
		BufferedWriter out1 = new BufferedWriter(fw1);
		
		int termIndex = 0;
		while (terms.next()) {
			//Write terms to file
			out1.write(terms.term().text() + "\n");
			
			// Write term document matrix to file
			TermDocs termDocs = indexReader.termDocs(terms.term());
			while (termDocs.next()) {
				out.write(termIndex + " " + termDocs.doc() + " "
						+ termDocs.freq() + "\n");
			}
			++termIndex;
		}
		out.close();
		out1.close();

	}
	
	public static double[][] buildTermDocMatrix(IndexReader indexReader) throws IOException{
		int numDocs = indexReader.numDocs();
		
		TermEnum terms = indexReader.terms();
		
		int numTerms = 0;
		while (terms.next()) {
			++numTerms;
		}
		double[][] termDocMatrix = new double[numTerms][numDocs];
		
		int termIndex = 0;
		terms = indexReader.terms();
		
		while (terms.next()) {
			TermDocs termDocs = indexReader.termDocs(terms.term());
			while (termDocs.next()) {
				termDocMatrix[termIndex][termDocs.doc()] = termDocs.freq();
			}
			++termIndex;
		}
		
		return termDocMatrix;
	}
	
	public static String getDocsListPath() {
		return docsListPath;
	}

	public static void setDocsListPath(String docsListPath) {
		TermDocumentUtils.docsListPath = docsListPath;
	}

	public static String getTermDocMatrixPath() {
		return termDocMatrixPath;
	}

	public static void setTermDocMatrixPath(String termDocMatrixPath) {
		TermDocumentUtils.termDocMatrixPath = termDocMatrixPath;
	}

	public static String getTermsListPath() {
		return termsListPath;
	}

	public static void setTermsListPath(String termsListPath) {
		TermDocumentUtils.termsListPath = termsListPath;
	}

}
