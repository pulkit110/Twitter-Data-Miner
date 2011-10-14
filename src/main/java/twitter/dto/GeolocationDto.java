/**
 * 
 */
package twitter.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;

/**
 * @author pulkit & sapan
 *
 */
@Entity
@Table(name = "geolocation")
public class GeolocationDto {
	@Id
	@GeneratedValue
	int id;
	
	double startLatitude;
	double startLongitude;
	double endLatitude;
	double endLongitude;
	
	public double getStartLatitude() {
		return startLatitude;
	}
	public void setStartLatitude(double startLatitude) {
		this.startLatitude = startLatitude;
	}
	public double getStartLongitude() {
		return startLongitude;
	}
	public void setStartLongitude(double startLongitude) {
		this.startLongitude = startLongitude;
	}
	public double getEndLatitude() {
		return endLatitude;
	}
	public void setEndLatitude(double endLatitude) {
		this.endLatitude = endLatitude;
	}
	public double getEndLongitude() {
		return endLongitude;
	}
	public void setEndLongitude(double endLongitude) {
		this.endLongitude = endLongitude;
	}
	
}
