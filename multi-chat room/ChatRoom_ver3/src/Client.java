import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
	private Socket server;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private boolean online;	//prepare for Reconnect
	
	//1. build socket to server
	//2. set outputStream, inputStream and username(Client ID)
	//3. new listener, thread
	//4. close(), sentMessage(), setStatus(), getStatus() functions
	public Client(String username, String ip, String port, JTextArea textArea,
			JTextField textField) throws UnknownHostException, IOException {
		server = new Socket(ip, Integer.parseInt(port));
		online = true;
		textArea.append("connect to server...OK\n");
			
		output = new ObjectOutputStream(server.getOutputStream());
		output.flush();
		input = new ObjectInputStream(server.getInputStream());

		output.writeUTF(username);
		output.flush();

		ClientListener listener = new ClientListener(server, input, output,
					textArea, this);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();	
	}

	public void close() throws IOException {
		server.close();
		output.close();
		input.close();
	}

	public void sentMessage(String message) {
		try {
			output.writeUTF(message);
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setStatus(boolean online){
		this.online = online;
	}
	public boolean getStatus(){
		return online;
	}
}

//get input and detect that if connection lost
class ClientListener implements Runnable {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket server;
	private JTextArea textArea;
	private Client client;
	
	public ClientListener(Socket server, ObjectInputStream input,
			ObjectOutputStream output, JTextArea textArea, Client client) {
		this.server = server;
		this.input = input;
		this.output = output;
		this.textArea = textArea;
		this.client = client;
	}

	@Override
	public void run() {
		while (true) {
			try {
				textArea.append(input.readUTF() + "\n");
			} catch (IOException e) {
				textArea.append("! Server Closed !\nEnter \"Reconnect\" to try if Server open again.\n");
				client.setStatus(false);
				try {
					server.close();
					input.close();
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}

		}
	}
}