package AsymmetricCrypto;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SecretKeyManager {

	private KeyPair keyPair;
	protected byte[] encryptedKey = new byte[128];
	protected Cipher cipher;
	protected PublicKey publicKey;
	protected byte[] key = new byte[16];

	public SecretKeyManager() {
		try {
			cipher = Cipher.getInstance("RSA");
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.generateKeyPair();
			this.publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public byte[] getEncryptedKey() {
		return encryptedKey;
	}

	public byte[] decryptKey(byte[] encrypted) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		PrivateKey privKey = keyPair.getPrivate();

		this.encryptedKey = encrypted;
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		key = cipher.doFinal(encryptedKey);
		return key;
	}
}