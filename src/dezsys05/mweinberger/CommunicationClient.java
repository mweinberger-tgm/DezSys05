package dezsys05.mweinberger;

import java.io.*;
import java.net.*;

/**
 * 
 * https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
 *
 */
public class CommunicationClient {

	private Socket socket;

	private DataInputStream eingabe;
	private DataOutputStream ausgabe;

	private String ip = "";
	private int portnr = 0;

	public CommunicationClient(String ip, int portnr) {
		this.ip = ip;
		this.portnr = portnr;
	}

	public void connect() {
		try {
			this.socket = new Socket(ip, portnr);
			this.eingabe = new DataInputStream(this.socket.getInputStream());
			this.ausgabe = new DataOutputStream(this.socket.getOutputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] read() {
		try {
			int length = this.eingabe.readInt();
			if (length > 0) {
				byte[] message = new byte[length];
				this.eingabe.readFully(message, 0, message.length);
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void write(byte[] bytes) {
		try {
			ausgabe.writeInt(bytes.length);
			ausgabe.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			ausgabe.close();
			eingabe.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
