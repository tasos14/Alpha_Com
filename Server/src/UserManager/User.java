package UserManager;
import java.io.Serializable;


public class User implements Serializable {
	

	private static final long serialVersionUID = 999L;
	private String username, email, name, location, telephone;
	
	public User(String usrnm) {
		username = usrnm;
		email = "-";
		name = "-";
		location = "-";
		telephone = "-";
		
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setTelephone(String tel) {
		this.telephone = tel;
	}

	public String toString() {
		return username;
	}

	public String getEmail() {
		return email;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean equals(User u) {
		if (email==u.getEmail())
				return true;
		return false;						
	}
}
