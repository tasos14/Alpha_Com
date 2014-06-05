package Packets;

import UserManager.*;

public class ConnectionPacket extends Packet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7166778302320207570L;
	private User user;
	
	public ConnectionPacket(User user) {
		super(Packet.CONNECTION);
		this.user = user;	
	}
	
	public User getUser() {
		return user;
	}
}
