import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

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

public class Server {
	private static int uniqueId;
	private ArrayList<ClientThread> clientThreads;
	private ServerGUI serverGUI;
	private SimpleDateFormat dateFormat;
	private int port;
	private boolean keepGoing;
	private UserManager userManager;
	private byte[] symmetricKey = new byte[16];
	private Encrypter encrypter;

	public Server(int port) {
		this(port, null);
	}

	public Server(int port, ServerGUI sg) {
		this.serverGUI = sg;
		this.port = port;
		dateFormat = new SimpleDateFormat("HH:mm:ss");

		clientThreads = new ArrayList<ClientThread>();
		userManager = new UserManager();

		SecureRandom random = new SecureRandom();
		random.nextBytes(symmetricKey);

		encrypter = new Encrypter(symmetricKey);
	}

	public void start() {
		keepGoing = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);

			while (keepGoing) {
				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept();
				if (!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);
				clientThreads.add(t);
				t.start();
			}

			try {
				serverSocket.close();
				for (int i = 0; i < clientThreads.size(); ++i) {
					ClientThread tc = clientThreads.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					} catch (IOException ioE) {
					}
				}
			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		} catch (IOException e) {
			String msg = dateFormat.format(new Date())
					+ " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}

	protected void stop() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		} catch (Exception e) {
		}
	}

	private void display(String msg) {
		String time = dateFormat.format(new Date()) + " " + msg;
		if (serverGUI == null)
			System.out.println(time);
		else
			serverGUI.appendEvent(time + "\n");
	}

	private synchronized void broadcast(Packet packet) {
		String time = dateFormat.format(new Date());

		switch (packet.getType()) {

		case Packet.MESSAGE:
			MessagePacket messagePacket = (MessagePacket) packet;
			String messageLf = time + " " + messagePacket.getSender() + ": "
					+ messagePacket.getMessage() + "\n";
			if (serverGUI == null)
				System.out.print(messageLf);
			else
				serverGUI.appendRoom(messageLf);

			for (int i = clientThreads.size(); --i >= 0;) {
				ClientThread ct = clientThreads.get(i);

				if (!ct.writePacket(messagePacket)) {
					clientThreads.remove(i);
					display("Disconnected Client " + ct.username
							+ " removed from list.");
				}
			}

			break;

		case Packet.WHO_IS_IN:
			WhoIsInPacket whoIsInPacket = (WhoIsInPacket) packet;

			String whoIsIn = "";
			for (User u : userManager.getUsers()) {
				whoIsIn += time + " " + u + " is online. " + "\n";
			}

			if (serverGUI == null)
				System.out.print(whoIsIn);
			else
				serverGUI.appendRoom(whoIsIn);

			for (int i = clientThreads.size(); --i >= 0;) {
				ClientThread ct = clientThreads.get(i);
				if (!ct.writePacket(whoIsInPacket)) {
					clientThreads.remove(i);
					display("Disconnected Client " + ct.username
							+ " removed from list.");
				}
			}
			break;

		case Packet.SOUND:
			SoundPacket soundPacket = (SoundPacket) packet;

			for (int i = clientThreads.size(); --i >= 0;) {
				ClientThread ct = clientThreads.get(i);

				if (!ct.writePacket(soundPacket)) {
					clientThreads.remove(i);
					display("Disconnected Client " + ct.username
							+ " removed from list.");
				}
			}

			break;
			
		case Packet.FILE:
			FilePacket filePacket = (FilePacket) packet;

			for (int i = clientThreads.size(); --i >= 0;) {
				ClientThread ct = clientThreads.get(i);

				if (!ct.writePacket(filePacket)) {
					clientThreads.remove(i);
					display("Disconnected Client " + ct.username
							+ " removed from list.");
				}
			}

			break;
		}
	}

	synchronized void remove(int id) {
		for (int i = 0; i < clientThreads.size(); ++i) {
			ClientThread ct = clientThreads.get(i);
			if (ct.id == id) {
				clientThreads.remove(i);
				return;
			}
		}
	}

	public static void main(String[] args) {
		int portNumber = 1500;
		switch (args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Server [portNumber]");
				return;
			}
		case 0:
			break;
		default:
			System.out.println("Usage is: > java Server [portNumber]");
			return;

		}
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		User user;
		String date;
		SecretKeyManager secretKeyManager;

		ClientThread(Socket socket) {
			id = ++uniqueId;
			this.socket = socket;
			System.out
					.println("Thread trying to create Object Input/Output Streams");
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());

				byte[] receivedBytes = (byte[]) sInput.readObject();

				ByteArrayInputStream ib = new ByteArrayInputStream(
						receivedBytes);
				ObjectInputStream i = new ObjectInputStream(ib);


				ConnectionPacket connectionPacket = (ConnectionPacket) i.readObject();
				this.user = connectionPacket.getUser();
				username = user.toString();
				display(username + " just connected.");
				userManager.addUser(connectionPacket.getUser());
				broadcast(new WhoIsInPacket(userManager));

				receivedBytes = (byte[]) sInput.readObject();
				ib = new ByteArrayInputStream(receivedBytes);
				i = new ObjectInputStream(ib);

				NegotiationPacket negotiationPacket = (NegotiationPacket) i
						.readObject();
				SecretKeyManager secretKeyManager;

				secretKeyManager = new SecretKeyManager(
						negotiationPacket.getPublicKey());
				secretKeyManager.encryptKey(symmetricKey);
				negotiationPacket.symmetricKeyIs(secretKeyManager
						.getEncryptedKey());

				ByteArrayOutputStream ob = new ByteArrayOutputStream();
				ObjectOutputStream o = new ObjectOutputStream(ob);
				o.writeObject(negotiationPacket);

				sOutput.writeObject(ob.toByteArray());

			} catch (IOException | ClassNotFoundException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}

			date = new Date().toString() + "\n";
		}

		public void run() {
			boolean keepGoing = true;
			while (keepGoing) {
				try {
					byte[] packetBytes = (byte[]) sInput.readObject();

					Packet packet = encrypter.decrypt(packetBytes);

					switch (packet.getType()) {

					case Packet.MESSAGE:
						MessagePacket messagePacket = (MessagePacket) packet;
						broadcast(messagePacket);
						break;

					case Packet.DISCONNECT:
						DisconnectPacket disconnectPacket = (DisconnectPacket) packet;
						display(username
								+ " disconnected with a LOGOUT message.");

						userManager.deleteUser(disconnectPacket.getUser());

						WhoIsInPacket whoIsInPacket = new WhoIsInPacket(
								userManager);
						broadcast(whoIsInPacket);

						keepGoing = false;
						break;
						
					case Packet.SOUND:
						SoundPacket soundPacket = (SoundPacket) packet;
						broadcast(soundPacket);
						break;
						
					case Packet.CONNECTION:
						ConnectionPacket connectionPacket = (ConnectionPacket) packet;
						
						System.out.println("COM PACK recieved");
						userManager.deleteUser(user);
						this.user = connectionPacket.getUser();
						userManager.addUser(user);
						
						WhoIsInPacket whoIsInPacket11 = new WhoIsInPacket(
								userManager);
						broadcast(whoIsInPacket11);
						break;
						
					case Packet.FILE:
						FilePacket filePacket = (FilePacket) packet;
						broadcast(filePacket);
						break;

					}
				} catch (IOException | ClassNotFoundException
						| InvalidKeyException | IllegalBlockSizeException
						| BadPaddingException e) {
					display(username + " Exception reading Streams: " + e);
					e.printStackTrace();
					break;
				}
			}
			remove(id);
			close();
		}

		private void close() {
			try {
				if (sOutput != null)
					sOutput.close();
			} catch (Exception e) {
			}
			try {
				if (sInput != null)
					sInput.close();
			} catch (Exception e) {
			}
			;
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
			}
		}

		private boolean writePacket(Packet packet) {
			if (!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(encrypter.encrypt(packet));
			} catch (IOException | InvalidKeyException
					| IllegalBlockSizeException | BadPaddingException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}