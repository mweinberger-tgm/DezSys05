package dezsys05.mweinberger;

import javax.crypto.*;
import javax.xml.bind.*;
import java.security.*;
import java.security.spec.*;

public class Client {

	private SecretKey symSchl;
	private PublicKey pk;
	private CommunicationClient communicationClient;
	private LDAPConnector ldap;

	public Client() {
		this.ldap = new LDAPConnector("127.0.0.1", 389, "cn=admin,dc=nodomain,dc=com", "user");
		this.pk = null;
		this.symSchl = erstelleSymKey();

		this.communicationClient = new CommunicationClient("127.0.0.1", 4711);
	}

	public PublicKey holePublicKey() {
		try {
			String ldapKey = this.ldap.getDescription();

			byte[] key = hexStringToByteArray(ldapKey);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(pubKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public SecretKey erstelleSymKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			return keygen.generateKey();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void schickeVerschlSymKey() {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, this.pk);
			byte[] ready = cipher.doFinal(this.symSchl.getEncoded());
			this.communicationClient.write(ready);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printMessage() {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, this.symSchl);
			byte[] ready = cipher.doFinal(this.communicationClient.read());
			System.out.println("Empfangene entschluesselte Nachricht: " + new String(ready));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		this.communicationClient.close();
		this.ldap.close();
	}

	private byte[] hexStringToByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}

	public CommunicationClient getClient() {
		return communicationClient;
	}

	public SecretKey getSymSchl() {
		return symSchl;
	}

	public void setSymSchl(SecretKey symSchl) {
		this.symSchl = symSchl;
	}

	public PublicKey getPublicKey() {
		return pk;
	}

	public void setPublicKey(PublicKey pk) {
		this.pk = pk;
	}

	public void setClient(CommunicationClient communicationClient) {
		this.communicationClient = communicationClient;
	}

	public LDAPConnector getConnector() {
		return ldap;
	}

	public void setConnector(LDAPConnector ldap) {
		this.ldap = ldap;
	}
}
