/**
 * 
 */
package twitter.dto;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import twitter4j.User;

/**
 * @author pulkit & sapan
 * 
 */
@Entity
@Table(name = "user")
public class UserDto {

	@Id
	long id;

	Date createdAt;
	String description;
	int favouritesCount;
	int followersCount;
	int friendsCount;
	boolean isGeoEnabled;
	boolean isVerified;
	String lang;
	int listedCount;
	String location;
	String name;
	String screenName;
	int statusesCount;
	String timeZone;
	String url;
	int utcOffset;
	int connectionDepth;
	boolean visited;

	@ManyToMany(cascade = CascadeType.ALL)
	Set<FollowerIdDto> followersIds;

	@ManyToMany(cascade = CascadeType.ALL)
	Set<FriendIdDto> friendsIds;

	public UserDto() {
		this.visited = false;
	}

	public UserDto(User u) {
		this.createdAt = (Date) u.getCreatedAt();
		this.description = u.getDescription();
		this.favouritesCount = u.getFavouritesCount();
		this.followersCount = u.getFollowersCount();
		this.friendsCount = u.getFriendsCount();
		this.id = u.getId();
		this.isGeoEnabled = u.isGeoEnabled();
		this.isVerified = u.isVerified();
		this.lang = u.getLang();
		this.listedCount = u.getListedCount();
		this.location = u.getLocation();
		this.name = u.getName();
		this.screenName = u.getScreenName();
		this.statusesCount = u.getStatusesCount();
		this.timeZone = u.getTimeZone();
		if (u.getURL() != null) {
			this.url = u.getURL().toString();
		}
		this.utcOffset = u.getUtcOffset();
		this.followersIds = new HashSet<FollowerIdDto>();
		this.friendsIds = new HashSet<FriendIdDto>();
		this.visited = false;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isGeoEnabled() {
		return isGeoEnabled;
	}

	public void setGeoEnabled(boolean isGeoEnabled) {
		this.isGeoEnabled = isGeoEnabled;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getListedCount() {
		return listedCount;
	}

	public void setListedCount(int listedCount) {
		this.listedCount = listedCount;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}

	public int getConnectionDepth() {
		return connectionDepth;
	}

	public void setConnectionDepth(int connectionDepth) {
		this.connectionDepth = connectionDepth;
	}

	public Set<FollowerIdDto> getFollowersIds() {
		return followersIds;
	}

	public void setFollowersIds(Set<FollowerIdDto> followersIds) {
		this.followersIds = followersIds;
	}

	public Set<FriendIdDto> getFriendsIds() {
		return friendsIds;
	}

	public void setFriendsIds(Set<FriendIdDto> friendsIds) {
		this.friendsIds = friendsIds;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

}
