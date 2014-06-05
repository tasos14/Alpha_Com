package Packets;

import UserManager.*;

public class ConnectionPacket extends Packet {
	
	private User user;
	private static final long serialVersionUID = -7166778302320207570L;
	
	public ConnectionPacket(User user) {
		super(Packet.CONNECTION);
		this.user = user;	
	}
	
	public User getUser() {
		return user;
	}
}
