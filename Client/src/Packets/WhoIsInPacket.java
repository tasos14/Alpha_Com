package Packets;

import java.util.ArrayList;

import UserManager.UserManager;


public class WhoIsInPacket extends Packet {
	
	private UserManager userManager;

	public WhoIsInPacket(UserManager userManager) {
		super(Packet.WHO_IS_IN);
		this.userManager = userManager;	
	}
	
	public UserManager getUserManager() {
		return userManager;
	}

}
