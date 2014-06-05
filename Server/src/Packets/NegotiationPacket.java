package Packets;

import UserManager.*;
import java.security.PublicKey;


public class NegotiationPacket extends Packet {
	
	private PublicKey clientPublicKey;
	private User user;
	private byte[] sessionSymmetricKey;

	public NegotiationPacket(PublicKey pk, User usr) {
		super(Packet.NEGOTIATION);
		clientPublicKey = pk;
		user = usr;
	}
	
	public void symmetricKeyIs(byte[] symmetricKey) {
		sessionSymmetricKey = symmetricKey;
	}
	
	public byte[] getSymmetricKey() {
		return sessionSymmetricKey;
	}

	public User getUser() {
		return user;
	}
	
	public PublicKey getPublicKey() {
		return clientPublicKey;
	}
}
