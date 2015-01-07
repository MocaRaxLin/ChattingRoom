import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import javax.swing.JTextArea;

public class Listener implements Runnable {
	private Server server;
	private LinkedList<IOGroup> clientList;
	private IOGroup ioGroup;
	private ObjectInputStream input;
	private JTextArea textArea;
	private String clientID;

	//keep getting message and detect lost connection of client
	public Listener(Server server, LinkedList<IOGroup> clientList,
			IOGroup group, JTextArea textArea, String clientID) {
		this.server = server;
		this.clientList = clientList;
		this.ioGroup = group;
		this.input = group.input;
		this.textArea = textArea;
		this.clientID = clientID;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String message = input.readUTF();
				textArea.append(clientID + ": " + message + "\n");
				server.sentMessage(clientID + ": " + message);
			} catch (IOException e) {
				textArea.append(clientID + " is left\n");
				ioGroup.close();
				clientList.remove(ioGroup);
				server.sentMessage(clientID + " is left");
				break;
			}
		}
	}
}
