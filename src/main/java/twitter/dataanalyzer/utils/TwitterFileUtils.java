package twitter.dataanalyzer.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TwitterFileUtils {

	public static void write(int[][] matrix, String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				out.write(matrix[i][j] + " ");
			}
			out.write("\n");
		}
		out.close();
	}

	public static void writeSparse(int[][] matrix, String path)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] != 0) {
					out.write(i + " " + j + "\n");
				}
			}
		}
		out.close();
	}

	public static void write(double[][] matrix, String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				out.write(matrix[i][j] + " ");
			}
			out.write("\n");
		}
		out.close();

	}

	public static void write(boolean[][] matrix, String path)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j])
					out.write(1 + " ");
				else
					out.write(0 + " ");
			}
			out.write("\n");
		}
		out.close();

	}

	public static void writeSparse(boolean[][] matrix, String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j])
					out.write(i + " " + j + "\n");
			}
		}
		out.close();

	}
}
