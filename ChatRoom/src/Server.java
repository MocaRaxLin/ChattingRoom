import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	private ServerSocket server;
	private LinkedList<IOGroup> clientList;
	private JTextField textField;
	public Server(JTextArea textArea, JTextField textField) throws IOException {
		this.textField = textField;
		clientList = new LinkedList<IOGroup>();
		server = new ServerSocket(4567, 3);
		
		Thread getClient = new Thread(new ClientThread(server,clientList,textArea));
		getClient.start();
	}

	public void sentMessage(){
		String message = textField.getText();
		for(int i = 0;i<clientList.size();i++){
			try {
				clientList.get(i).output.writeUTF(message);
				clientList.get(i).output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
	public void close() throws IOException {
		server.close();
	}

}

class ClientThread implements Runnable{
	private ServerSocket server;
	private LinkedList<IOGroup> clientList;
	private JTextArea textArea;
	public ClientThread(ServerSocket server, LinkedList<IOGroup> clientList, JTextArea textArea){
		this.server = server;
		this.clientList = clientList;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		while(true){
			System.out.println("wait for connection...");
			Socket client;
			try {
				client = server.accept();
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
			
			//manage by IO Group
			IOGroup currentGroup = new IOGroup(client, output, input);
			clientList.add(currentGroup);
			
			String clientID = "unknown id";
			try {
				clientID = currentGroup.input.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Listener listener = new Listener(clientList,currentGroup,textArea,clientID);
			Thread listenerThread = new Thread(listener);
			listenerThread.start();
			
		}
	}
}
