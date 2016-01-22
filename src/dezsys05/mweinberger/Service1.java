package dezsys05.mweinberger;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.bind.*;
import java.security.*;

public class Service1 {

	private KeyPair keyPair;
	private SecretKey symKey;
	private LDAPConnector connector;
	private CommunicationServer communicationServer;

	public Service1() {
		try {
			this.keyPair = generiereKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.connector = new LDAPConnector("127.0.0.1", 389, "cn=admin,dc=nodomain,dc=com", "user");
		this.communicationServer = new CommunicationServer(4711);

	}

	private KeyPair generiereKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		generator.initialize(1024, random);
		KeyPair keypair = generator.generateKeyPair();
		System.out.println("Schluesselpaar wurde ezeugt");
		return keypair;
	}

	public void speicherePublicKey() {
		System.out.println("Public Key wurde in " + LDAPConnector.GROUP + " geschrieben");
		this.connector.setDescription(byteArrayToHexString(this.keyPair.getPublic().getEncoded()));
	}

	public void entschlSymKey() {
		try {
			System.out.println("Symmetrischer Schluessel wird mit Private Key entschluesselt...");
			byte[] encryptedSymKey = this.communicationServer.listenOnce();
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			byte[] ready = cipher.doFinal(encryptedSymKey);
			this.symKey = new SecretKeySpec(ready, 0, ready.length, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void schickeVerschlNachricht(String message) {
		if (this.symKey != null) {
			try {
				System.out.println("Verschluesselte Nachricht wird an Server uebermittelt: " + message);
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, this.symKey);

				if (message == null) {
					System.out.println("Nachricht = null! Abbruch");
					System.exit(1);
				}
				byte[] ready = cipher.doFinal((message).getBytes());
				this.communicationServer.getOut().writeInt(ready.length);
				this.communicationServer.getOut().write(ready);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new NullPointerException();
		}
	}

	public void close() {
		this.communicationServer.close();
		this.connector.close();
	}

	private String byteArrayToHexString(byte[] array) {
		String hex = DatatypeConverter.printHexBinary(array);
		return hex;
	}

	public CommunicationServer getServer() {
		return communicationServer;
	}
}
