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
@Table(name = "mentionEntity")
public class MentionEntityDto {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long generatedId;

	String mentionedScreenName;
	long statusId;

	public MentionEntityDto() {
		super();
	}

	public MentionEntityDto(String mentionedScreenName, long statusID) {
		super();
		this.mentionedScreenName = mentionedScreenName;
		this.statusId = statusID;
	}

	public String getMentionedScreenName() {
		return mentionedScreenName;
	}

	public void setMentionedScreenName(String mentionedScreenName) {
		this.mentionedScreenName = mentionedScreenName;
	}

	public long getMentionedStatusID() {
		return statusId;
	}

	public void setMentionedStatusID(long statusID) {
		this.statusId = statusID;
	}
}
