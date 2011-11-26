/**
 * 
 */
package twitter.dataanalyzer.utils;

/**
 * @author sapan and pulkit
 *
 */
public class CosineDocCluster {

	// We will normalize by taking square root of frequencies
	// http://alias-i.com/lingpipe/demos/tutorial/cluster/read-me.html
	double[][] normalizedTermDocMatrix;
	
	public CosineDocCluster(double[][] termDocMatrix) {
		normalizedTermDocMatrix = new double[termDocMatrix.length][termDocMatrix[0].length];
		for (int i = 0; i < termDocMatrix.length; ++i) {
			for (int j = 0; j < termDocMatrix[i].length; ++j) {
				normalizedTermDocMatrix[i][j] = Math.sqrt(termDocMatrix[i][j]);
			}
		}
	}

}
