import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import UserManager.User;


public class ProfileFrame extends JFrame {
	
	public ProfileFrame(User user) {
		super("Profile Frame");
		JLabel usernameLabel = new JLabel("Username: " + user);
		JLabel emailLabel = new JLabel("Email: " + user.getEmail());
		JLabel nameLabel = new JLabel("Name: " + user.getName());
		JLabel locationLabel = new JLabel("Location: " + user.getLocation());
		JLabel telephoneLabel = new JLabel("Telephone: " + user.getTelephone());
		
		JPanel panel = new JPanel(new GridLayout(5, 1));
		
		panel.add(usernameLabel, BorderLayout.CENTER);
		panel.add(emailLabel, BorderLayout.CENTER);
		panel.add(nameLabel, BorderLayout.CENTER);
		panel.add(locationLabel, BorderLayout.CENTER);
		panel.add(telephoneLabel, BorderLayout.CENTER);
		
		this.setContentPane(panel);
		this.setSize(230,200);
		this.setResizable(false);
		this.setLocation(200, 200);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}

}
