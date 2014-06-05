package Packets;
import java.io.Serializable;



public abstract class Packet implements Serializable{
	
	public static final int MESSAGE = 1;
	public static final int CONNECTION = 2;
	public static final int NEGOTIATION = 3;
	public static final int WHO_IS_IN = 4;
	public static final int DISCONNECT = 5;
	public static final int SOUND = 6;
	public static final int FILE = 7;
	
	protected int type;
	
	public Packet(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
