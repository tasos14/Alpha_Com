package Packets;

import UserManager.User;

public class DisconnectPacket extends Packet {
	
	private User user;

	public DisconnectPacket(User user) {
		super(Packet.DISCONNECT);
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

}
