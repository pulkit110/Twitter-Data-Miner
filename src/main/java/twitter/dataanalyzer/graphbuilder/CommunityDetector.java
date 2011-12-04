/**
 * 
 */
package twitter.dataanalyzer.graphbuilder;

import java.util.List;

import twitter.dto.UserDto;

/**
 * @author pulkit and sapan
 *
 */
public interface CommunityDetector {

	List<List<UserDto>> cluster(int[][] A, List<UserDto> users, int k);
	List<List<UserDto>> cluster(boolean[][] A, List<UserDto> users, int k);
}
