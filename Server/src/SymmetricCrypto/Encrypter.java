package SymmetricCrypto;
import Packets.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {

	private final String AES = "AES";
	private Cipher cipher;
	private SecretKeySpec secretKey;

	public Encrypter(byte[] key){
		try {

			secretKey = new SecretKeySpec(key, AES);
			cipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static byte[] serialize(Packet p){
		ByteArrayOutputStream b = new ByteArrayOutputStream();

		try {

			ObjectOutputStream o;
			o = new ObjectOutputStream(b);
			o.writeObject(p);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return b.toByteArray();
	}

	public static Packet deserialize(byte[] bytes) throws ClassNotFoundException, IOException{
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return (Packet)o.readObject();
	}

	public Cipher getCipher() {
		return cipher;
	}

	public byte[] encrypt(Packet packet) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(serialize(packet));
	}

	public Packet decrypt(byte[] cryptoPacket) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return deserialize(cipher.doFinal(cryptoPacket));
	}
}