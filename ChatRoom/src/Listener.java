import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import javax.swing.JTextArea;

public class Listener implements Runnable {
	private LinkedList<IOGroup> clientList;
	private IOGroup ioGroup;
	private ObjectInputStream input;
	private JTextArea textArea;
	private String clientID;

	public Listener(LinkedList<IOGroup> clientList, IOGroup group, JTextArea textArea, String clientID) {
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
				textArea.append(clientID + ": " + input.readUTF()+"\n");
			} catch (IOException e) {
				textArea.append(clientID+" is left\n");
				ioGroup.close();
				clientList.remove(ioGroup);
				break;
			}
		}
	}
}
