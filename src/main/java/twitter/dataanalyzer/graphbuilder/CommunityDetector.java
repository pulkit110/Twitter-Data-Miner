/**
 * 
 */
package twitter.dataanalyzer.graphbuilder;

/**
 * @author pulkit and sapan
 *
 */
public interface CommunityDetector {

	void cluster(int[][] A);
	void cluster(boolean[][] A);
}
