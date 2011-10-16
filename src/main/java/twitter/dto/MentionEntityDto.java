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
public class MentionEntityDto {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long generatedId;

	String mentionedScreenName;
//	long mentionedStatusID;

	public MentionEntityDto() {
		super();
	}

	public MentionEntityDto(String mentionedScreenName, long mentionedStatusID) {
		super();
		this.mentionedScreenName = mentionedScreenName;
//		this.mentionedStatusID = mentionedStatusID;
	}

	public String getMentionedScreenName() {
		return mentionedScreenName;
	}

	public void setMentionedScreenName(String mentionedScreenName) {
		this.mentionedScreenName = mentionedScreenName;
	}

//	public long getMentionedStatusID() {
//		return mentionedStatusID;
//	}

	public void setMentionedStatusID(long mentionedStatusID) {
//		this.mentionedStatusID = mentionedStatusID;
	}
}
