/**
 * 
 */
package twitter.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import twitter4j.Place;

/**
 * @author pulkit and sapan
 * 
 */
@Entity
@Table(name = "place")
public class PlaceDto {

	@Id
	String id;
	String countryCode;
	String country;
	String placeType;
	String streetAddress;
	String name;
	String fullName;

	public PlaceDto(Place place) {
		id = place.getId();
		countryCode = place.getCountryCode();
		country = place.getCountry();
		placeType = place.getPlaceType();
		streetAddress = place.getStreetAddress();
		name = place.getName();
		fullName = place.getFullName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
