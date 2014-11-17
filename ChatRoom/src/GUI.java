import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GUI{
	private JFrame frame;
	private JPanel startPanel;
	private JPanel workingPanel;
	private JTextArea textArea;
	private JTextField textField;
	public GUI(){
		
		setFrame();
		setStartPanel();
		setWorkingPanel();
	}

	private void setFrame() {
		frame = new JFrame("ChatRoom");
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(100, 50);
		frame.setVisible(true);
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
					System.out.println(textField.getText());
					textField.setText("");
				}
			}
		});
		
		//set scroll for text area and determine where are the components
		workingPanel.add(new JScrollPane(textArea),BorderLayout.CENTER);
		workingPanel.add(textField,BorderLayout.PAGE_END);
		
		frame.add(workingPanel);		
	}

	private void setStartPanel() {
		startPanel = new JPanel();
		JButton btnServer = new JButton("Server");
		JButton btnClient = new JButton("Client");
		startPanel.setLayout(new FlowLayout());
		
		//set action listener for button
		btnServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("server button");
				startPanel.setVisible(false);
			}
		});
		btnClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("client button");
				startPanel.setVisible(false);
			}
		});
		startPanel.add(btnServer);
		startPanel.add(btnClient);
		startPanel.setVisible(true);
		frame.add(startPanel);
	}
}
