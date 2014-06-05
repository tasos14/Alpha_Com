package UserManager;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.DefaultListModel;


public class UserManager implements Serializable {
	
	private static final long serialVersionUID = 1000L;
	private ArrayList<User> users;
	
	public UserManager() {	
		users = new ArrayList<User>();
	}
	
	public void addUser(User u) {
		users.add(u);
		System.out.println("Server Alert // User '" + u + "' added to the user list of the server!");
	}
	
	public boolean containsUser(User u) {
		if (users.contains(u))
				return true;
		return false;
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}
	
	public DefaultListModel<User> getListModel() {
		DefaultListModel<User> listModel = new DefaultListModel<User>();
		for (User u : users) {
			listModel.addElement(u);
		}
		return listModel;
	}
	
	public void deleteUser(User u) {
		users.remove(u);
		System.out.println("Server Alert // User '" + u + "' removed from the user list of the server!");
	}
}

