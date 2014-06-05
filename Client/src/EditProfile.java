import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Packets.ConnectionPacket;
import UserManager.User;


public class EditProfile extends JFrame implements ActionListener{
	
	private JButton save;
	private Client client;
	private JTextField username, email, location, name, telephone;
	

	public EditProfile(Client client){
		super("Edit Profile");
		
		this.client = client;
		JButton save = new JButton("Save");
		save.addActionListener(this);
		username = new JTextField(client.getUser().toString());
		email = new JTextField(client.getUser().getEmail());
		location = new JTextField(client.getUser().getLocation());
		name = new JTextField(client.getUser().getName());
		telephone = new JTextField(client.getUser().getTelephone());
		
		JPanel northPanel = new JPanel(new GridLayout(5,2));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		northPanel.add(new JLabel("Username:"));
		northPanel.add(username);
		northPanel.add(new JLabel("Email:"));
		northPanel.add(email);
		northPanel.add(new JLabel("Location:"));
		northPanel.add(location);
		northPanel.add(new JLabel("Name:"));
		northPanel.add(name);
		northPanel.add(new JLabel("Telephone:"));
		northPanel.add(telephone);
		panel.add(northPanel);
		panel.add(save);

		this.setSize(250, 250);
		this.setLocation(200,200);
		this.setContentPane(panel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		User u = new User(username.getText());
		u.setEmail(email.getText());
		u.setLocation(location.getText());
		u.setName(name.getText());
		u.setTelephone(telephone.getText());
		client.setUser(u);
		
		ConnectionPacket connectionPacket = new ConnectionPacket(u);		
		client.sendPacket(connectionPacket);
		this.dispose();		
	}

}
