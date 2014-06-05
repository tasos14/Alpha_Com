package Packets;

import UserManager.*;
import java.io.*;

public class MessagePacket extends Packet implements Serializable {

	protected static final long serialVersionUID = 1112122200L;
	
	private String message;
	private User sender;

	public MessagePacket(String message) {
		super(Packet.MESSAGE);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public User getSender() {
		return sender;
	}
}
