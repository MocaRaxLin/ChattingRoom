import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GUI{
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
	public GUI(){
		setFrame();
		setWorkingPanel();
		setStartPanel();
		frame.setVisible(true);
	}

	private void setFrame() {
		frame = new JFrame("ChatRoom");
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 50);
		
	}

	private void setWorkingPanel() {
		//set layout for the working panel
		workingPanel = new JPanel(new BorderLayout());
		
		//textArea is an object to show message we type
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		
		//textField is a blank where we input the message
		textField = new JTextField();
		textField.addKeyListener(new KeyListener() {
			
			//unused functions
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			
			//the message-input function
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					textArea.append(username+": "+textField.getText()+"\n");
					if(status.equals("server")){
						server.sentMessage();
					}else if(status.equals("client")){
						client.sentMessage();
					}
					textField.setText("");
				}
			}
		});
		
		//set scroll for text area and determine where are the components
		workingPanel.add(new JScrollPane(textArea),BorderLayout.CENTER);
		workingPanel.add(textField,BorderLayout.PAGE_END);
	}

	private void setStartPanel() {
		JButton btnServer = new JButton("Server");
		JButton btnClient = new JButton("Client");
		
		//set action listener for button
		btnServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				username = "Admin";
				System.out.println("server:"+username);
				frame.setTitle("ChatRoom-"+username);
				frame.add(workingPanel);
				frame.remove(startPanel);
				frame.setVisible(true);
				
				try {
					openServer();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		btnClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(nameTextfield.getText().equals("")){
					JOptionPane.showMessageDialog(startPanel, "ID cannot be empty for client!");
				}else{
					username = nameTextfield.getText();
					System.out.println("client:" + username);
					frame.setTitle("ChatRoom-"+username);
					frame.add(workingPanel);
					frame.remove(startPanel);
					frame.setVisible(true);
					
					try {
						openClient(username);
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
					
				}
				
			}
		});
		
		//id-input tools
		JLabel idLabel = new JLabel("   ID:   ");		
		nameTextfield = new JTextField(28);
		
		//add to start panel
		startPanel = new JPanel();	
		startPanel.add(idLabel);
		startPanel.add(nameTextfield);
		startPanel.add(btnServer);
		startPanel.add(btnClient);		
		startPanel.setVisible(true);
		
		frame.add(startPanel);
	}
	
	private void openServer() throws IOException{
		status = "server";
		server = new Server(textArea,textField);
	}
	
	private void openClient(String username) throws UnknownHostException, IOException{
		status = "client";
		client = new Client(username,"127.0.0.1",textArea,textField);
	}
}
