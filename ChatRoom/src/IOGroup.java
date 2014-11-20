import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOGroup {
	public Socket socket;
	public ObjectOutputStream output;
	public ObjectInputStream input;

	public IOGroup(Socket socket, ObjectOutputStream output,
			ObjectInputStream input) {
		this.socket = socket;
		this.input = input;
		this.output = output;
	}

	public void close() {
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("IOGroup close...error");
		}

	}
}
