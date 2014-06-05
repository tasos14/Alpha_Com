package AsymmetricCrypto;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SecretKeyManager {

	protected byte[] encryptedKey = new byte[128];
	protected Cipher cipher;
	protected PublicKey publicKey;
	protected byte[] key = new byte[16];

	public SecretKeyManager(PublicKey publicKey){
		try {

			cipher = Cipher.getInstance("RSA");
			this.publicKey = publicKey;

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

	public void encryptKey(byte[] secretKey){		
		try {
			
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedKey = cipher.doFinal(secretKey);
			
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
}