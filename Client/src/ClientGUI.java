import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Packets.DisconnectPacket;
import Packets.FilePacket;
import Packets.MessagePacket;
import UserManager.UserManager;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField tf, un;
	private JTextField tfServer, tfPort;
	private JButton login, logout, send, rec, sendFile, editProfile;
	private JTextArea ta;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;
	private JList userList;
	private UserManager userManager;
	private JFrame logI, chatF;

	ClientGUI(String host, int port) {

		logI = new JFrame("Log In");
		chatF = new JFrame("Chat Client");

		defaultPort = port;
		defaultHost = host;

		JPanel northPanel = new JPanel(new GridLayout(3, 1));
		JPanel serverAndPort = new JPanel(new GridLayout(4, 5, 1, 3));
		tfServer = new JTextField(host);
		tfPort = new JTextField(port + "");
		tfPort.setHorizontalAlignment(SwingConstants.LEFT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		un = new JTextField("Anonumus");
		serverAndPort.add(new JLabel("User Name:  "));
		serverAndPort.add(un);

		login = new JButton("Login");
		login.addActionListener(this);
		serverAndPort.add(login);

		logI.add(serverAndPort);

		label = new JLabel("Enter your message below", SwingConstants.CENTER);

		tf = new JTextField("");
		tf.setBackground(Color.WHITE);

		logI.add(northPanel, BorderLayout.NORTH);

		ta = new JTextArea(50, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 1, 3));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);

		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);

		send = new JButton("Send");
		send.addActionListener(this);
		send.setEnabled(false);

		sendFile = new JButton("Send File");
		sendFile.addActionListener(this);
		sendFile.setEnabled(false);

		editProfile = new JButton("Edit Profile");
		editProfile.addActionListener(this);
		editProfile.setEnabled(false);

		rec = new JButton("Record");
		rec.addActionListener(this);
		rec.setEnabled(false);

		userList = new JList();
		userList.setFixedCellHeight(25);
		userList.setFixedCellWidth(130);
		userList.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		userList.setPreferredSize(new Dimension(20, 468));
		userList.setVisible(true);
		userList.addMouseListener(new MouseAdapter(this));

		centerPanel.add(new JScrollPane(userList));
		chatF.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new GridLayout(2, 8, 1, 3));
		JPanel snPanel = new JPanel(new GridLayout(2, 1, 1, 3));
		JPanel sPanel = new JPanel(new GridLayout(1, 1, 1, 3));

		snPanel.add(label);
		snPanel.add(tf);
		southPanel.add(send);
		southPanel.add(logout);
		southPanel.add(sendFile);
		southPanel.add(rec);
		southPanel.add(editProfile);

		sPanel.add(snPanel);
		sPanel.add(southPanel);

		chatF.add(sPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		logI.setSize(600, 600);
		logI.setVisible(true);
		tf.requestFocus();
		logI.pack();

	}

	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}

	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		send.setEnabled(false);
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		connected = false;
	}

	public void updateListModel(DefaultListModel listModel) {
		userList.setModel(listModel);
		userList.repaint();
	}

	public JList getUserList() {
		return userList;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == logout) {
			DisconnectPacket disconnectPacket = new DisconnectPacket(
					client.getUser());
			client.sendPacket(disconnectPacket);
			chatF.dispose();

			return;
		} else if (e.getSource() == editProfile) {
			new EditProfile(client);
			return;
		} else if (e.getSource() == rec) {
			Record record = new Record(client);

			return;
		} else if (e.getSource() == send) {
			client.sendPacket(new MessagePacket(tf.getText(), client.getUser()));
			tf.setText("");
			return;
		} else if (e.getSource() == sendFile) {

			JFileChooser fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				Path path = Paths.get(file.getAbsolutePath());
				try {
					byte[] data = Files.readAllBytes(path);
					FilePacket fp = new FilePacket(data, file.getName(),
							client.getUser());
					client.sendPacket(fp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			} else {
				System.out.println("File access cancelled by user.");
			}
		} else if (e.getSource() == login) {
			String username = un.getText().trim();
			if (username.length() == 0)
				return;
			String server = tfServer.getText().trim();
			if (server.length() == 0)
				return;

			String portNumber = tfPort.getText().trim();
			if (portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			} catch (Exception en) {
				return;
			}

			try {
				client = new Client(server, port, username, this);

			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				e1.printStackTrace();
			}
			if (!client.start())
				return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			login.setEnabled(false);
			logout.setEnabled(true);
			send.setEnabled(true);
			rec.setEnabled(true);
			editProfile.setEnabled(true);
			sendFile.setEnabled(true);
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tf.addActionListener(this);
			chatF.setVisible(true);
			chatF.setSize(600, 600);
			logI.dispose();
		}

	}

	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}

}
