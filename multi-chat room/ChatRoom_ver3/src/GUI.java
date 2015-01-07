import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUI {
	private JFrame frame;

	private JPanel startPanel;
	private JTextField nameTextfield;

	private JPanel workingPanel;
	private JTextArea textArea;
	private JTextField textField;
	private String username;
	private Server server;
	private Client client;
	private String status = "none";
	private JTextField ipTextfield;
	private JTextField portTextfield;
	private String ip;
	private String port;
	
	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		setFrame();
		setWorkingPanel();
		setStartPanel();
		frame.setVisible(true);
	}

	private void setFrame() {	//to place StartPanel and WorkingPanel
		frame = new JFrame("ChatRoom");
		frame.setSize(720, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(200, 100);
	}

	private void setWorkingPanel() {	//panel of chat room 
		// set layout for the working panel
		workingPanel = new JPanel(new BorderLayout());

		// textArea is an object to show message we type
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);

		// textField is a blank where we input the message
		textField = new JTextField();
		textField.addKeyListener(new KeyListener() {

			// unused functions
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}

			// the message-input function
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = textField.getText();
					if(!message.equals("")){ //sent message
						if (status.equals("server")) {
							textArea.append("Admin: " + message + "\n");
							server.sentMessage("Admin: " + message);
						} else if (status.equals("client")) {
							if((!client.getStatus())&&message.equals("Reconnect")){
								try {
									openClient(username, ip, port);
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(startPanel,
											"connecting Error!");
								}
							}else	client.sentMessage(message);
						}
						textField.setText("");
					}
				}
			}
		});

		// set scroll for text area and determine where are the components
		workingPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		workingPanel.add(textField, BorderLayout.PAGE_END);
	}

	private void setStartPanel() {
		JButton btnServer = new JButton("Server");
		JButton btnClient = new JButton("Client");

		// set action listener for button
		//button to open server and have basic steps to prevent errors
		btnServer.addActionListener(new ActionListener() {	
			private String localIP;

			@Override
			public void actionPerformed(ActionEvent e) {
				username = "Admin";
				try {
					localIP = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e2) {
					JOptionPane.showMessageDialog(startPanel,
							"Cannot get local IP!");
				}
				port = portTextfield.getText();
				int intport = 0;
				try{
					intport = Integer.parseInt(port);
					if (intport < 1024 || 65535 < intport) {
						JOptionPane.showMessageDialog(startPanel,
								"Port should be in the range 1024~65535!");
					} else {
						frame.setTitle("ChatRoom- " + username + " on ip: "
								+ localIP + " on port: " + port);
						frame.add(workingPanel);
						frame.remove(startPanel);
						frame.setVisible(true);

						try {
							openServer(port);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(startPanel,
									"Server-opening Error!");
						}
					}
				}catch(NumberFormatException e3){  
			    	JOptionPane.showMessageDialog(startPanel,
							"Port should be in the range 1024~65535!");  
			    }
			}
		});
		
		//button to open client and have basic steps to prevent errors
		btnClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (nameTextfield.getText().equals("")) {
					JOptionPane.showMessageDialog(startPanel,
							"ID cannot be empty for client!");
				} else if (ipTextfield.getText().equals("")) {
					JOptionPane.showMessageDialog(startPanel,
							"IP cannot be empty for client!");
				} else if (portTextfield.getText().equals("")) {
					JOptionPane.showMessageDialog(startPanel,
							"Port cannot be empty for client!");
				} else {

					username = nameTextfield.getText();
					ip = ipTextfield.getText();
					port = portTextfield.getText();

					int intport = 0;
					try{  
						intport = Integer.parseInt(port); 
						if (intport < 1024 || 65535 < intport||intport == 0) {
							JOptionPane.showMessageDialog(startPanel,
									"Port should be in the range 1024~65535!");
						} else {
							try {
								openClient(username, ip, port);
								frame.setTitle("ChatRoom-" + username + " on ip:" + ip
										+ " on port:" + port);
								frame.add(workingPanel);
								frame.remove(startPanel);
								frame.setVisible(true);
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(startPanel,
										"Client-opening Error!");
							}
						}
				    } catch(NumberFormatException e3){  
				    	JOptionPane.showMessageDialog(startPanel,
								"Port should be in the range 1024~65535!");  
				    }
					
				}

			}
		});

		// id-input tools
		JLabel idLabel = new JLabel("ID :");
		nameTextfield = new JTextField(28);

		// Ip & port
		JLabel ipLabel = new JLabel("IP :");
		ipTextfield = new JTextField(17);
		JLabel portLabel = new JLabel("Port :");
		portTextfield = new JTextField(6);

		// add to start panel
		startPanel = new JPanel();
		startPanel.add(idLabel);
		startPanel.add(nameTextfield);
		startPanel.add(ipLabel);
		startPanel.add(ipTextfield);
		startPanel.add(portLabel);
		startPanel.add(portTextfield);
		startPanel.add(btnServer);
		startPanel.add(btnClient);
		startPanel.setVisible(true);

		frame.add(startPanel);
	}

	private void openServer(String port) throws IOException {
		status = "server";	//use in keyReleased function at working panel
		server = new Server(port, textArea, textField);
		//details are in "Server" class
	}

	private void openClient(String username, String ip, String port)
			throws UnknownHostException, IOException {
		status = "client";	//use in keyReleased function at working panel
		client = new Client(username, ip, port, textArea, textField);
		//details are in "Client" class
	}

}
