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

	public Client(String username, String ip, String port, JTextArea textArea,
			JTextField textField) throws UnknownHostException, IOException {
		server = new Socket(ip, Integer.parseInt(port));
		textArea.append("connect to server...OK\n");

		output = new ObjectOutputStream(server.getOutputStream());
		output.flush();
		input = new ObjectInputStream(server.getInputStream());

		output.writeUTF(username);
		output.flush();

		ClientListener listener = new ClientListener(server, input, output,
				textArea);
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
}

class ClientListener implements Runnable {
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket server;
	private JTextArea textArea;

	public ClientListener(Socket server, ObjectInputStream input,
			ObjectOutputStream output, JTextArea textArea) {
		this.server = server;
		this.input = input;
		this.output = output;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		while (true) {
			try {
				textArea.append(input.readUTF() + "\n");
			} catch (IOException e) {
				textArea.append("! Server Closed !\n");
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