import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import AsymmetricCrypto.SecretKeyManager;
import Packets.ConnectionPacket;
import Packets.DisconnectPacket;
import Packets.FilePacket;
import Packets.MessagePacket;
import Packets.NegotiationPacket;
import Packets.Packet;
import Packets.SoundPacket;
import Packets.WhoIsInPacket;
import SymmetricCrypto.Encrypter;
import UserManager.User;
import UserManager.UserManager;

public class Client {

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	
	//just a comment for no reason :P

	private ClientGUI clientGUI;

	private String server;
	private int port;

	private User user;
	private Play play;
	private UserManager userManager;
	private SecretKeyManager secretKeyManager;
	private byte[] secretKey = new byte[16];
	private Encrypter encrypter;

	private SimpleDateFormat sdf;

	Client(String server, int port, String username, ClientGUI cg)
			throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.server = server;
		this.port = port;
		this.user = new User(username);
		this.clientGUI = cg;
		this.secretKeyManager = new SecretKeyManager();
		sdf = new SimpleDateFormat("HH:mm:ss");
	}

	Client(String server, int port, String username)
			throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.server = server;
		this.port = port;
		this.user = new User(username);
		this.secretKeyManager = new SecretKeyManager();
		sdf = new SimpleDateFormat("HH:mm:ss");
	}

	public User getUser() {
		return user;
	}

	public void setUser(User u) {
		user = u;
		ConnectionPacket cp = new ConnectionPacket(u);
		sendPacket(cp);
	}

	public boolean start() {
		try {
			socket = new Socket(server, port);
		} catch (Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":"
				+ socket.getPort();
		display(msg);

		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());

		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		try {
			ConnectionPacket connectionPacket = new ConnectionPacket(user);
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(connectionPacket);
			sOutput.writeObject(b.toByteArray());
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}

		try {

			NegotiationPacket negotiationPacket = new NegotiationPacket(
					secretKeyManager.getPublicKey(), user);

			ByteArrayOutputStream ob = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(ob);
			o.writeObject(negotiationPacket);

			sOutput.writeObject(ob.toByteArray());

			byte[] packetBytes = (byte[]) sInput.readObject();

			ByteArrayInputStream ib = new ByteArrayInputStream(packetBytes);
			ObjectInputStream i = new ObjectInputStream(ib);
			Packet packet = (Packet) i.readObject();

			negotiationPacket = (NegotiationPacket) packet;
			secretKey = secretKeyManager.decryptKey(negotiationPacket
					.getSymmetricKey());
			encrypter = new Encrypter(secretKey);

		} catch (IOException | ClassNotFoundException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		ConnectionPacket connectionPacket11 = new ConnectionPacket(user);

		sendPacket(connectionPacket11);

		new ListenFromServer().start();

		return true;
	}

	private void display(String msg) {
		if (clientGUI == null)
			System.out.println(msg);
		else
			clientGUI.append(msg + "\n");
	}

	void sendPacket(Packet packet) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);

			switch (packet.getType()) {

			case Packet.MESSAGE:
				MessagePacket messagePacket = (MessagePacket) packet;
				byte[] messageBytes = encrypter.encrypt(messagePacket);
				sOutput.writeObject(messageBytes);
				break;

			case Packet.DISCONNECT:
				DisconnectPacket disconnectPacket = (DisconnectPacket) packet;
				byte[] disconnectBytes = encrypter.encrypt(disconnectPacket);
				sOutput.writeObject(disconnectBytes);
				break;
			case Packet.SOUND:
				SoundPacket sp = (SoundPacket) packet;
				byte[] soundBytes = encrypter.encrypt(sp);
				sOutput.writeObject(soundBytes);
				break;

			case Packet.FILE:
				FilePacket fp = (FilePacket) packet;
				byte[] fileBytes = encrypter.encrypt(fp);
				sOutput.writeObject(fileBytes);
				break;

			case Packet.CONNECTION:
				ConnectionPacket cp = (ConnectionPacket) packet;
				byte[] connectionBytes = encrypter.encrypt(cp);
				sOutput.writeObject(connectionBytes);
				break;
			}

		}

		catch (IOException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			display("Exception writing to server: " + e);
		}
	}

	private void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		}
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		}
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}

		if (clientGUI != null)
			clientGUI.connectionFailed();

	}

	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch (args.length) {
		case 3:
			serverAddress = args[2];
		case 2:
			try {
				portNumber = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out
						.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
				return;
			}
		case 1:
			userName = args[0];
		case 0:
			break;
		default:
			System.out
					.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		Client client;
		try {
			client = new Client(serverAddress, portNumber, userName);

			if (!client.start())
				return;

			Scanner scan = new Scanner(System.in);
			System.out.print("> ");
			String msg = scan.nextLine();
			while (true) {

				if (msg.equalsIgnoreCase(".LOGOUT")) {
					client.sendPacket(new DisconnectPacket(client.getUser()));
					break;
				}

				else {
					client.sendPacket(new MessagePacket(msg, client.getUser()));
				}
			}
			client.disconnect();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	class ListenFromServer extends Thread {

		public void run() {

			while (true) {
				try {
					byte[] encryptedPacket = (byte[]) sInput.readObject();
					Packet packet = encrypter.decrypt(encryptedPacket);

					switch (packet.getType()) {

					case Packet.MESSAGE:
						MessagePacket messagePacket = (MessagePacket) packet;
						String time = sdf.format(new Date());
						String output = time+" : "+ messagePacket.getSender()+ "--> " + messagePacket.getMessage()
								+ "\n";

						if (clientGUI == null) {
							System.out.println(output);
							System.out.print("> ");
						} else {
							clientGUI.append(output);
						}
						break;

					case Packet.WHO_IS_IN:
						WhoIsInPacket whoIsInPacket = (WhoIsInPacket) packet;
						userManager = whoIsInPacket.getUserManager();					
						clientGUI.updateListModel(userManager.getListModel());
						break;

					case Packet.SOUND:
						SoundPacket sp = (SoundPacket) packet;
						play = new Play(sp.getAudio());
						break;

					case Packet.FILE:
						FilePacket fp = (FilePacket) packet;

						File file = new File(fp.getFilename());

						Path path = Paths.get(file.getAbsolutePath());
						try {
							Files.write(path, fp.getByteArray());
							JOptionPane.showMessageDialog(
									null,
									"Received file with name '"
											+ fp.getFilename() + "' from "
											+ fp.getSender(), "Alert",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					}

				} catch (IOException | ClassNotFoundException
						| InvalidKeyException | IllegalBlockSizeException
						| BadPaddingException e) {
					display("Server has close the connection: " + e);
					if (clientGUI != null)
						clientGUI.connectionFailed();
					break;
				}
			}
		}
	}
}
