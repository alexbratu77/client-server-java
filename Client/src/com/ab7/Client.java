package com.ab7;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
	
	private JTextField textArea;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client(String host) {
		super("ALEX MESSENGER");
		serverIP = host;
		textArea = new JTextField();
		textArea.setEditable(false);
		textArea.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					textArea.setText("");
				}
			}	
		);
		add(textArea, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(500,500);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofException) {
			showMessage("\n End of connection...");
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}finally {
			closeMess();
		}
	}
	
	public void connectToServer() throws IOException{
		showMessage("\n Connecting to server... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("\n Connected to "+ connection.getInetAddress().getHostName());
	}
	
	public void setupStreams() throws IOException{
		output =  new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n All the streams are configured!");
	}
	
	public void whileChatting() throws IOException{
		showMessage("\n You can now start chatting!");
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n "+ message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Object type not found...");
			}
		}while(!message.equals("SERVER - END"));
		
		
	}
	
	public void closeMess() {
		showMessage("\n Connection closing...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\n CLIENT - " + message);
		}catch(IOException ioException) {
			chatWindow.append("\n ERROR: Something went wrong...");
		}
	}
	
	public void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}	
		);
	}
	
	public void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					textArea.setEditable(tof);
				}
			}	
		);
	}
	
	
	
	
}