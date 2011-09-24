/**
 * 
 */
package Twitter.DTO;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author pulkit & sapan
 * 
 */
@Entity
@Table(name = "status")
public class StatusDto {

	@Id
	private long id;
	
	private String inReplyToScreenName;
	private long inReplyToStatusId;
	private long inReplyToUserId;
	private String place;
	private long retweetCount;
	private String source;
	private String text;
	private String user;
	private boolean isFavorited;
	private boolean isNew;

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public long getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(long retweetCount) {
		this.retweetCount = retweetCount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isFavorited() {
		return isFavorited;
	}

	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}
}
