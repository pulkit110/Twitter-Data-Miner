package twitter.dataanalyzer;

import google.utils.GAddress;
import google.utils.GCoder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import twitter.dto.UserDto;
import dbutils.HibernateUtil;

public class VisualizeGraphCountry {

	Session session;
	Transaction transaction;
	
	
	
	public static void main(String args[]) throws Exception {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		Map<String, Integer> frequencyByCountry = new Hashtable<String, Integer>();

		Query q = session.createQuery("from UserDto");
		q.setMaxResults(1000);
		List<UserDto> users = q.list();
		
		for (UserDto u : users) {
			if (u.getLocation() == null) {
				continue;
			}
			
			GAddress gAddress = GCoder.geocode(u.getLocation());
			if (gAddress != null) {
				String countryName = gAddress.getCountryName();
				if (frequencyByCountry.containsKey(countryName)) {
					frequencyByCountry.put(countryName, frequencyByCountry.get(countryName)+1);
				} else {
					frequencyByCountry.put(countryName, 1);
				}
			}
		}
		
		FileWriter fw = new FileWriter("UserCountryData.csv");
		BufferedWriter out = new BufferedWriter(fw);
		
		for (String country : frequencyByCountry.keySet()) {
			out.write(country + "," + frequencyByCountry.get(country) + "," + country);
			out.write("\n");
		}
		
		out.close();
		
	}
}
