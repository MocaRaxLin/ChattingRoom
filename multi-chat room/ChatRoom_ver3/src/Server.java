import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	private ServerSocket serverSocket;
	private LinkedList<IOGroup> clientList;

	//1. new client list, serverSocket, thread(ClientThread)
	//2. setMessage(), close() functions
	public Server(String port, JTextArea textArea, JTextField textField) throws IOException {
		clientList = new LinkedList<IOGroup>();
		serverSocket = new ServerSocket(Integer.parseInt(port), 8);

		Thread getClient = new Thread(new ClientThread(this, serverSocket,
				clientList, textArea));
		getClient.start();
	}

	public void sentMessage(String message) {
		for (int i = 0; i < clientList.size(); i++) {
			try {
				clientList.get(i).output.writeUTF(message);
				clientList.get(i).output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() throws IOException {
		serverSocket.close();
	}

}

//accept client
//new input/ output stream
//give data to IOGroup
//append message about new user
//new thread(listener)
class ClientThread implements Runnable {
	private Server server;
	private ServerSocket serverSocket;
	private LinkedList<IOGroup> clientList;
	private JTextArea textArea;

	public ClientThread(Server server, ServerSocket serverSocket,
			LinkedList<IOGroup> clientList, JTextArea textArea) {
		this.server = server;
		this.serverSocket = serverSocket;
		this.clientList = clientList;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("wait for connection...");
			Socket client;
			try {
				client = serverSocket.accept();
			} catch (IOException e) {
				System.out.println("get client...error");
				continue;
			}
			System.out.println("get client...OK");

			System.out.println("Wait for IO Stream Connection...");
			ObjectOutputStream output;
			ObjectInputStream input;
			try {
				output = new ObjectOutputStream(client.getOutputStream());
				output.flush();
				input = new ObjectInputStream(client.getInputStream());
			} catch (IOException e) {
				System.out.println("get IOStream...error");
				continue;
			}
			System.out.println("get IOStream...OK");

			// manage by IO Group
			IOGroup currentGroup = new IOGroup(client, output, input);
			clientList.add(currentGroup);
			
			String clientID = "unknown id";
			try {
				clientID = currentGroup.input.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}
			textArea.append(clientID+" got in the room\n");
			server.sentMessage(clientID+" got in the room");
			
			Listener listener = new Listener(server, clientList, currentGroup,
					textArea, clientID);
			Thread listenerThread = new Thread(listener);
			listenerThread.start();

		}
	}
}
