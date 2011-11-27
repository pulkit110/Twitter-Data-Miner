/**
 * 
 */
package twitter.dataanalyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import twitter.dto.StatusDto;

import dbutils.HibernateUtil;

/**
 * @author pulkit
 * 
 */
public class AnalyzeTweetsWithKeyword {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();

		buildCSVForAppleLocation(session, transaction);

	}

	private static void buildCSVForManUtdLocation(Session session,
			Transaction transaction) throws IOException {
		Criteria c = session.createCriteria(StatusDto.class);

		c.add(Restrictions.isNotNull("geoLocationLatitude"));

		c.add(Restrictions.or(Restrictions.or(Restrictions.or(Restrictions.or(
				Restrictions.or(Restrictions.like("text", "%manchester%"),
						Restrictions.like("text", "%manutd%")), Restrictions
						.like("text", "%manu%")), Restrictions.like("text",
				"mufc")), Restrictions.like("text", "manutd")), Restrictions.like("text", "man utd")));

		@SuppressWarnings("unchecked")
		List<StatusDto> statuses = c.list();

		FileWriter fw = new FileWriter("ManutdLocations.csv");
		BufferedWriter out = new BufferedWriter(fw);

		for (StatusDto s : statuses) {
			out.write(s.getCreatedAt() + "," + s.getGeoLocationLatitude() + ","
					+ s.getGeoLocationLongitude());
			out.write('\n');
		}
		out.close();
	}
	
	private static void buildCSVForAppleLocation(Session session,
			Transaction transaction) throws IOException {
		Criteria c = session.createCriteria(StatusDto.class);

		c.add(Restrictions.isNotNull("geoLocationLatitude"));

		c.add(Restrictions.or(Restrictions.or(Restrictions.or(Restrictions.or(Restrictions.or(Restrictions.or(Restrictions.or(
				Restrictions.or(Restrictions.like("text", "%apple%"),
						Restrictions.like("text", "%ipod%")), Restrictions
						.like("text", "%ipad%")), Restrictions.like("text",
				"iphone")), Restrictions.like("text", "ios")), Restrictions.like("text", "itunes")),
				Restrictions.like("text", "mac")),Restrictions.like("text", "os x")),Restrictions.like("text", "osx")));

		@SuppressWarnings("unchecked")
		List<StatusDto> statuses = c.list();

		FileWriter fw = new FileWriter("AppleLocations.csv");
		BufferedWriter out = new BufferedWriter(fw);

		for (StatusDto s : statuses) {
			out.write(s.getCreatedAt() + "," + s.getGeoLocationLatitude() + ","
					+ s.getGeoLocationLongitude());
			out.write('\n');
		}
		out.close();
	}
}
