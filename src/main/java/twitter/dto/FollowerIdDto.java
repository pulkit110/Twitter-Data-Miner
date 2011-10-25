/**
 * 
 */
package twitter.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author sapan and pulkit
 *
 */
@Entity
@Table(name = "followersId")
public class FollowerIdDto {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long generatedId;
	
	long followerId;
	long userId;

	public FollowerIdDto(long id, long userId) {
		super();
		this.followerId = id;
		this.userId = userId;
	}
	

	public FollowerIdDto() {
		super();
	}

	public long getId() {
		return followerId;
	}

	public void setId(long id) {
		this.followerId = id;
	}
}
