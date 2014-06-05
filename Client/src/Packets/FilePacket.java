package Packets;

import UserManager.*;
import java.io.*;

public class FilePacket extends Packet implements Serializable {

	protected static final long serialVersionUID = 1112122201L;
	
	private byte[] file;
	private String filename;
	private User sender;

	public FilePacket(byte[] file, String filename, User sender) {
		super(Packet.FILE);
		this.filename = filename;
		this.file = file;
		this.sender = sender;
	}
	
	public byte[] getByteArray() {
		return file;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public User getSender() {
		return sender;
	}
}
