package com.ab;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

	private JTextField textArea;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		super("ALEX MESSENGER");
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
		add(new JScrollPane(chatWindow));
		setSize(500, 500);
		setVisible(true);
		
	}
	
	public void startRunning() {
		try {
			server = new ServerSocket(6789,50);
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException) {
					showMessage("\n End of connection...");
				}finally {
					closeMess();
				}
			}
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void waitForConnection() throws IOException{
		showMessage("\n Waiting for connection...\n");
		connection =  server.accept();
		showMessage("\n Connected to " + connection.getInetAddress().getHostName());		
	}
	
	public void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n All the streams are configured!");
	}
	
	public void whileChatting() throws IOException{
		String message = "\n You can now start chatting!!!";
		showMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n "+message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n Object type not found...");
			}
		}while(!message.equals("CLIENT - END"));
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
			output.writeObject("SERVER - "+message);
			output.flush();
			showMessage("\n SERVER - "+message);
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
