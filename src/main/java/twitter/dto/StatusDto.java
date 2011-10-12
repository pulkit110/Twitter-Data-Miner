/**
 * 
 */
package twitter.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 * @author pulkit & sapan
 * 
 */
@Entity
@Table(name = "status")
public class StatusDto {

	public static final int TYPE_FOLLOW = 1;
	public static final int TYPE_LOCATION = 2;
	public static final int TYPE_KEYWORD = 4;
	
	@Id
	private long id;

	private int type;
	
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
	private boolean isRetweet;
	private boolean isRetweetedByMe;
	private boolean isTruncated;
	private boolean isNew;
	private Double geoLocationLatitude;
	private Double geoLocationLongitude;
	private Date createdAt;
	@ElementCollection
	private List<Integer> startHashTagIndex;
	@ElementCollection
	private List<Integer> endHashTagIndex;
	@ElementCollection
	private List<String> mediaUrls;
	@ElementCollection
	private List<String> urlEntities;
	@ElementCollection
	private List<Long> userMentionIds;
	

	/**
	 * Default Constructor
	 */
	public StatusDto() {

	}

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
		this.setRetweet(status.isRetweet());
		this.setRetweetedByMe(status.isRetweetedByMe());
		if (status.getGeoLocation() != null) {
			this.setGeoLocationLatitude(status.getGeoLocation().getLatitude());
			this.setGeoLocationLongitude(status.getGeoLocation().getLongitude());
		}
		this.setCreatedAt(status.getCreatedAt());
		this.setTruncated(status.isTruncated());
		this.setFavorited(status.isFavorited());
		if (status.getMediaEntities() != null) {
			this.mediaUrls = new ArrayList<String>();
			for (MediaEntity mediaEntity : status.getMediaEntities()) {
				this.mediaUrls.add(mediaEntity.getMediaURL().toString());
			}	
		}
		
		if (status.getHashtagEntities() != null) {
			this.startHashTagIndex = new ArrayList<Integer>();
			this.endHashTagIndex = new ArrayList<Integer>();
			for (HashtagEntity hashTagEntity : status.getHashtagEntities()) {
				this.startHashTagIndex.add(hashTagEntity.getStart());
				this.endHashTagIndex.add(hashTagEntity.getEnd());
			}
		}
		
		if (status.getURLEntities() != null) {
			this.urlEntities = new ArrayList<String>();
			for (URLEntity urlEntity : status.getURLEntities()) {
				this.urlEntities.add(urlEntity.getURL().toString());
			}
		}
		
		if (status.getUserMentionEntities() != null) {
			this.userMentionIds = new ArrayList<Long>();
			for (UserMentionEntity u : status.getUserMentionEntities()) {
				this.userMentionIds.add(u.getId());
			}
		}
		
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

	public Double getGeoLocationLatitude() {
		return geoLocationLatitude;
	}

	public void setGeoLocationLatitude(Double geoLocationLatitude) {
		this.geoLocationLatitude = geoLocationLatitude;
	}

	public Double getGeoLocationLongitude() {
		return geoLocationLongitude;
	}

	public void setGeoLocationLongitude(Double geoLocationLongitude) {
		this.geoLocationLongitude = geoLocationLongitude;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public void setRetweet(boolean isRetweet) {
		this.isRetweet = isRetweet;
	}

	public boolean isRetweetedByMe() {
		return isRetweetedByMe;
	}

	public void setRetweetedByMe(boolean isRetweetedByMe) {
		this.isRetweetedByMe = isRetweetedByMe;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<Integer> getStartHashTagIndex() {
		return startHashTagIndex;
	}

	public void setStartHashTagIndex(List<Integer> startHashTagIndex) {
		this.startHashTagIndex = startHashTagIndex;
	}

	public List<Integer> getEndHashTagIndex() {
		return endHashTagIndex;
	}

	public void setEndHashTagIndex(List<Integer> endHashTagIndex) {
		this.endHashTagIndex = endHashTagIndex;
	}

	public List<String> getMediaUrls() {
		return mediaUrls;
	}

	public void setMediaUrls(List<String> mediaUrls) {
		this.mediaUrls = mediaUrls;
	}

	public List<String> getUrlEntities() {
		return urlEntities;
	}

	public void setUrlEntities(List<String> urlEntities) {
		this.urlEntities = urlEntities;
	}

	public List<Long> getUserMentionIds() {
		return userMentionIds;
	}

	public void setUserMentionIds(List<Long> userMentionIds) {
		this.userMentionIds = userMentionIds;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
