package Packets;

import UserManager.*;
import java.io.*;

public class MessagePacket extends Packet implements Serializable {

	protected static final long serialVersionUID = 1112122200L;
	
	private String message;
	private User sender;

	public MessagePacket(String message, User sender) {
		super(Packet.MESSAGE);
		this.message = message;
		this.sender = sender;
	}
	
	public String getMessage() {
		return message;
	}
	
	public User getSender() {
		return sender;
	}
}
