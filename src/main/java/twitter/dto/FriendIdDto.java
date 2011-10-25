/**
 * 
 */
package twitter.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * @author sapan and pulkit
 * 
 */
@Entity
@Table(name = "friendsId")
public class FriendIdDto {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	long generatedId;
	
	long friendId;
	long userId;

	public FriendIdDto(long id, long userId) {
		super();
		this.friendId = id;
		this.userId = userId;
	}

	public long getId() {
		return friendId;
	}

	public void setId(long id) {
		this.friendId = id;
	}

	public FriendIdDto() {
		super();
	}

}
