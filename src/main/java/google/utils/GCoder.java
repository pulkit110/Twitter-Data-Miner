package google.utils;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.hibernate.impl.QueryImpl;

public class GCoder {
	private static final String URL = "http://maps.google.com/maps/geo?output=json";
	private static final String DEFAULT_KEY = "AIzaSyCyVG8EIT-ICXO5N44ahq-IXUtzK9PVICw";

	public static GAddress geocode(String address, String key) throws Exception {
		URL url = new URL(URL + "&q=" + URLEncoder.encode(address, "UTF-8")
				+ "&key=" + key);
		URLConnection conn = url.openConnection();
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
		IOUtils.copy(conn.getInputStream(), output);
		output.close();

		String s = output.toString();
		GAddress gaddr = new GAddress();
		JSONObject json = JSONObject.fromObject(output.toString());
		if (json.getJSONObject("Status").getInt("code") != 200) {
			return null;
		}
		JSONObject placemark = (JSONObject) query(json, "Placemark[0]");

		final String commonId = "AddressDetails.Country.AdministrativeArea";

		gaddr.setFullAddress(query(placemark, "address").toString());
		gaddr.setZipCode(query(
				placemark,
				commonId
						+ ".SubAdministrativeArea.Locality.PostalCode.PostalCodeNumber")
				.toString());
		gaddr.setAddress(query(
				placemark,
				commonId
						+ ".SubAdministrativeArea.Locality.Thoroughfare.ThoroughfareName")
				.toString());
		gaddr.setCity(query(placemark,
				commonId + ".SubAdministrativeArea.SubAdministrativeAreaName")
				.toString());
		gaddr.setState(query(placemark, commonId + ".AdministrativeAreaName")
				.toString());
		gaddr.setLat(Double
				.parseDouble(query(placemark, "Point.coordinates[1]")
						.toString()));
		gaddr.setLng(Double
				.parseDouble(query(placemark, "Point.coordinates[0]")
						.toString()));

		gaddr.setCountryName(query(placemark, "AddressDetails.Country.CountryName").toString());
		gaddr.setCountryCode(query(placemark, "AddressDetails.Country.CountryNameCode").toString());
		return gaddr;
	}

	public static GAddress geocode(String address) throws Exception {
		return geocode(address, DEFAULT_KEY);
	}

	/* allow query for json nested objects, ie. Placemark[0].address */
	private static Object query(JSONObject jo, String query) {
		try {
			String[] keys = query.split("\\.");
			Object r = queryHelper(jo, keys[0]);
			for (int i = 1; i < keys.length; i++) {
				r = queryHelper(jo.fromObject(r), keys[i]);
			}
			return r;
		} catch (JSONException e) {
			return "";
		}
	}

	/* help in query array objects: Placemark[0] */
	private static Object queryHelper(JSONObject jo, String query) {
		int openIndex = query.indexOf('[');
		int endIndex = query.indexOf(']');
		if (openIndex > 0) {
			String key = query.substring(0, openIndex);
			int index = Integer.parseInt(query.substring(openIndex + 1,
					endIndex));
			return jo.getJSONArray(key).get(index);
		}
		return jo.get(query);
	}

	public static void main(String[] args) throws Exception {
		GAddress abc = GCoder.geocode("Indore, India");
		System.out
				.println(GCoder.geocode("650 Townsend st, San Francsico, CA"));
		System.out.println(GCoder.geocode("94103"));
	}
}
// ABQIAAAAdaexNcmdTGjUOs3Pu_MykhSRCIgguXOLihS2HlrF0GGQ4cYAHhRiV9uFpf5zPfZhTL9-V2UfCkjgBA