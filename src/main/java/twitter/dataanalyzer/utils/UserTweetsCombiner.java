/**
 * 
 */
package twitter.dataanalyzer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import dbutils.HibernateUtil;

import twitter.dto.StatusDto;
import twitter.dto.UserDto;

/**
 * @author pulkit and sapan
 *
 */
public class UserTweetsCombiner {

	private String documentDir;
	private File documentDirectory;
	private List<UserDto> users;
	
	public UserTweetsCombiner(String documentDir) {
		setDocumentDir(documentDir);
	}

	public List<File> generateTweetFiles() throws IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		List<File> userTweetFiles = new ArrayList<File>();
		
		for (UserDto u : users) {
			Criteria c = session.createCriteria(StatusDto.class);
			List<StatusDto> statuses = c.list();
			c.add(Restrictions.eq("screenname", u.getScreenName()));
			
			File f = new File(documentDir+File.separator+u.getScreenName());
			userTweetFiles.add(f);
			FileWriter fw = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fw);
			for (StatusDto s : statuses) {
				out.write(s.getText() + "\n");
			}

		}
		
		return userTweetFiles;
	}
	
	public void setDocumentDir(String documentDir) {
		this.documentDir = documentDir;
		documentDirectory = new File(documentDir);
		
		if (!documentDirectory.exists()) {
			documentDirectory.mkdirs();
		}
	}
	
	public List<UserDto> getUsers() {
		return users;
	}

	public void setUsers(List<UserDto> users) {
		this.users = users;
	}
	
	
}
