package dezsys05.mweinberger;

import java.io.*;
import java.net.*;

/**
 * 
 * https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
 *
 */
public class CommunicationServer {

	private ServerSocket srv;
	private Socket clt;
	private DataInputStream eingabe;
	private DataOutputStream ausgabe;

	public CommunicationServer(int port) {
		try {
			this.srv = new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		new Thread(() -> {
			try {
				clt = srv.accept();

				eingabe = new DataInputStream(clt.getInputStream());
				ausgabe = new DataOutputStream(clt.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}).start();
	}

	public byte[] listenOnce() {
		try {
			int length = eingabe.readInt();
			byte[] message = new byte[length];
			eingabe.readFully(message, 0, message.length);

			return message;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void close() {
		try {
			ausgabe.close();
			eingabe.close();
			clt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DataOutputStream getOut() {
		return ausgabe;
	}
}
