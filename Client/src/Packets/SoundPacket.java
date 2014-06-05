package Packets;



import UserManager.*;
import java.io.*;

public class SoundPacket extends Packet implements Serializable {

	protected static final long serialVersionUID = 1112122202L;
	
	private byte[] audio;
	private User sender;

	public SoundPacket(byte[] audio, User sender) {
		super(Packet.SOUND);
		this.audio = audio;
		this.sender = sender;
	}
	
	public byte[] getAudio() {
		return audio;
	}
	
	public User getSender() {
		return sender;
	}
}
