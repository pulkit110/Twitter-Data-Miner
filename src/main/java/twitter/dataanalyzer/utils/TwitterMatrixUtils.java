package twitter.dataanalyzer.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;

import com.aliasi.matrix.SvdMatrix;

/**
 * 
 * @author pulkit and sapan
 * 
 */
public class TwitterMatrixUtils {

	static final String DocsCosineSimilarityFilePath = "tmp/docsCosineSimilarity.txt";
	private static final String DocsLSASimilarityFilePath = "tmp/docsLSASimilarity.txt";
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
			String uName = d.getField("path").stringValue();
			if (uName.indexOf('.') != -1) {
				uName = uName.substring(0, uName.indexOf('.'));
			}
			
			out2.write(uName+ "\n");
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
					docILength += (termDocMatrix[k][i] * termDocMatrix[k][i]);
					docJLength += (termDocMatrix[k][j] * termDocMatrix[k][j]);
					docsCosineSimilarityMatrix[i][j] += termDocMatrix[k][i]
							* termDocMatrix[k][j];
				}
				docsCosineSimilarityMatrix[i][j] /= Math.sqrt(docILength
						* docJLength);

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

	public static SvdMatrix createSvdMatrix(double[][] termDocMatrix)
			throws IOException {

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

	public static double[][] docsLSASimilarity(double[][] termDocMatrix)
			throws IOException {
		return docsLSASimilarity(termDocMatrix, true);
	}

	public static double[][] docsLSASimilarity(double[][] termDocMatrix,
			boolean writeToFile) throws IOException {

		SvdMatrix svdMatrix = createSvdMatrix(termDocMatrix);

		int nTerms = termDocMatrix.length;
		int nDocs = termDocMatrix[0].length;

		FileWriter fw;
		BufferedWriter out = null;

		if (writeToFile) {
			fw = new FileWriter(DocsLSASimilarityFilePath);
			out = new BufferedWriter(fw);
		}

		double[][] docsLSASimilarityMatrix = new double[nDocs][nDocs];

		double[] scales = svdMatrix.singularValues();
		double[][] rightSingularVectors = svdMatrix.rightSingularVectors();

		for (int i = 0; i < nDocs; ++i) {
			for (int j = 0; j < nDocs; ++j) {
				double docILength = 0.0;
				double docJLength = 0.0;
				// Iterate through every term to calculate vector dot product
				for (int k = 0; k < scales.length; ++k) {
					double sqrtScale = Math.sqrt(scales[k]);
					double scaledDocI = sqrtScale * rightSingularVectors[i][k];
					double scaledDocJ = sqrtScale * rightSingularVectors[j][k];
					docILength += scaledDocI * scaledDocI;
					docJLength += scaledDocJ * scaledDocJ;

					docsLSASimilarityMatrix[i][j] += scaledDocI * scaledDocJ;
				}

				docsLSASimilarityMatrix[i][j] /= Math.sqrt(docILength
						* docJLength);

				if (writeToFile) {
					out.write(docsLSASimilarityMatrix[i][j] + " ");
				}
			}

			if (writeToFile) {
				out.write("\n");
			}
		}

		if (writeToFile) {
			out.close();
		}

		return docsLSASimilarityMatrix;
	}

	public static List getUserList() throws IOException {
		FileReader fr = new FileReader(docsListPath);
		BufferedReader in = new BufferedReader(fr);

		List<String> screenNames = new ArrayList<String>();

		String line = in.readLine();
		while (line != null && !line.isEmpty()) {
			screenNames.add(line);
			line = in.readLine();
		}

		return screenNames;

	}

	public static String getDocsListPath() {
		return docsListPath;
	}

	public static void setDocsListPath(String docsListPath) {
		TwitterMatrixUtils.docsListPath = docsListPath;
	}

	public static String getTermDocMatrixPath() {
		return termDocMatrixPath;
	}

	public static void setTermDocMatrixPath(String termDocMatrixPath) {
		TwitterMatrixUtils.termDocMatrixPath = termDocMatrixPath;
	}

	public static String getTermsListPath() {
		return termsListPath;
	}

	public static void setTermsListPath(String termsListPath) {
		TwitterMatrixUtils.termsListPath = termsListPath;
	}

	public static boolean[][] toGraph(double[][] matrix,
			double threshold) {
		boolean[][] graph = new boolean[matrix.length][matrix[0].length];
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > threshold) {
					graph[i][j] = true;
				} else {
					graph[i][j] = false;
				}
			}
		}
		return graph;
	}
}
