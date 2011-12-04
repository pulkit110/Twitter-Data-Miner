/**
 * 
 */
package twitter.dataanalyzer.utils;

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import twitter.dto.UserDto;

/**
 * @author pulkit and sapan
 */
public class KMeans {

	public List<List<UserDto>> cluster(double[][] features, List<UserDto> users, int k, int maxEpochs) {
		if (users.size() < k) {
			return null;
		}

		// Initialize the clusters
		List<List<UserDto>> clusters = new ArrayList<List<UserDto>>();
		double[][] centroids = new double[k][features[0].length];
		// List<UserDto> centroids = new ArrayList<UserDto>();
		for (int i = 0; i < k; ++i) {
			clusters.add(new ArrayList<UserDto>());
			for (int j = 0; j < features[i].length; ++j) {
				centroids[i][j] = features[i][j];
			}
		}

		for (int epoch = 0; epoch < maxEpochs; ++epoch) {
			for (int i = 0; i < clusters.size(); ++i) {
				clusters.get(i).clear();
			}

			// Add points to clusters
			for (int i = 0; i < features.length; ++i) {
				int j = findClosest(features[i], centroids);
				clusters.get(j).add(users.get(i));
			}

			// Recalculate centroids
			for (int i = 0; i < centroids.length; ++i) {
				for (int j = 0; j < centroids[i].length; ++j) {
					centroids[i][j] = 0;
				}
				for (int l = 0; l < clusters.get(i).size(); ++l) {
					for (int j = 0; j < centroids[i].length; ++j) {
						centroids[i][j] += features[users.indexOf(clusters.get(i).get(l))][j];
					}
				}
				for (int j = 0; j < centroids[i].length; ++j) {
					centroids[i][j] /= clusters.get(i).size();
				}
			}
		}
		return clusters;
	}

	private int findClosest(double[] vector, double[][] centroids) {
		double minDist = dist(vector, centroids[0]);
		int minIndex = 0;
		for (int j = 1; j < centroids.length; ++j) {
			double currDist = dist(vector, centroids[j]);
			if (currDist < minDist) {
				minIndex = j;
				minDist = currDist;
			}
		}
		return minIndex;
	}

	private double dist(double[] f1, double[] f2) {
		double dist = 0.0;
		for (int i = 0; i < f1.length; ++i) {
			dist += f1[i] * f2[i];
		}
		return dist;
	}

}
