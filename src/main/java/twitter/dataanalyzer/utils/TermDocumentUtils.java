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

	static final String DocsCosineSimilarityFilePath = "tmp/docsCosineSimilarity.txt";
	static String docsListPath = "tmp/docs.txt";
	static String termDocMatrixPath = "tmp/termdoc.txt";
	static String termsListPath = "tmp/terms.txt";

	public static void printTermFrequenciesToFile(IndexReader indexReader)
			throws IOException {

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
			// Write terms to file
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

	public static double[][] buildTermDocMatrix(IndexReader indexReader)
			throws IOException {
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

	public static double[][] docsCosineSimilarity(double[][] termDocMatrix)
			throws IOException {
		return (docsCosineSimilarity(termDocMatrix, true));
	}

	public static double[][] docsCosineSimilarity(double[][] termDocMatrix,
			boolean writeToFile) throws IOException {
		int nTerms = termDocMatrix.length;
		int nDocs = termDocMatrix[0].length;

		FileWriter fw;
		BufferedWriter out = null;

		if (writeToFile) {
			fw = new FileWriter(DocsCosineSimilarityFilePath);
			out = new BufferedWriter(fw);
		}
		double[][] docsCosineSimilarityMatrix = new double[nDocs][nDocs];

		
		for (int i = 0; i < nDocs; ++i) {
			for (int j = 0; j < nDocs; ++j) {
				double docILength = 0.0;
				double docJLength = 0.0;
				// Iterate through every term to calculate vector dot product
				for (int k = 0; k < nTerms; ++k) {
					docILength += (termDocMatrix[k][i]*termDocMatrix[k][i]);
					docJLength += (termDocMatrix[k][j]*termDocMatrix[k][j]);
					docsCosineSimilarityMatrix[i][j] += termDocMatrix[k][i]
							* termDocMatrix[k][j];
				}
				docsCosineSimilarityMatrix[i][j] /= Math.sqrt(docILength*docJLength);

				if (writeToFile) {
					out.write(docsCosineSimilarityMatrix[i][j] + " ");
				}
			}

			if (writeToFile) {
				out.write("\n");
			}
		}

		if (writeToFile) {
			out.close();
		}

		return docsCosineSimilarityMatrix;
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
