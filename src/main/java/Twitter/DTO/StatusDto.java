/**
 * 
 */
package Twitter.DTO;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import twitter4j.Status;

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

	@JoinColumn(name = "placeId")
	@ManyToOne(targetEntity = PlaceDto.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private PlaceDto place;

	private long retweetCount;
	private String source;
	private String text;
	private String user;
	private boolean isFavorited;
	private boolean isNew;

	public StatusDto(Status status) {
		this.setFavorited(status.isFavorited());
		this.setId(status.getId());
		this.setInReplyToScreenName(status.getInReplyToScreenName());
		this.setInReplyToStatusId(status.getInReplyToStatusId());
		this.setInReplyToUserId(status.getInReplyToUserId());
		if (status.getPlace() != null) {
			this.setPlace(new PlaceDto(status.getPlace()));
		}
		this.setRetweetCount(status.getRetweetCount());
		this.setSource(status.getSource());
		this.setText(status.getText());
		this.setUser(status.getUser().getName());
		this.setNew(true);
	}

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

	public PlaceDto getPlace() {
		return place;
	}

	public void setPlace(PlaceDto place) {
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

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNew() {
		return isNew;
	}
}
