package dezsys05.mweinberger;

import javax.swing.*;

/**
 * 
 * DezSys05 | Java Security
 * 
 * @author mweinberger
 *
 */
public class JavaSecurity {

	public static void main(String[] args) {
		Service1 service = new Service1();
		Client client = new Client();

		service.speicherePublicKey();
		
		client.setPublicKey(client.holePublicKey());
		
		service.getServer().start();
		
		client.getClient().connect();
		client.schickeVerschlSymKey();
		
		service.entschlSymKey();
		service.schickeVerschlNachricht((JOptionPane.showInputDialog(null, "Geben Sie eine Nachricht ein!", "DezSys05 Weinb 5BHIT", JOptionPane.PLAIN_MESSAGE)));
		
		client.printMessage();
		
		client.close();
		service.close();
	}
}
