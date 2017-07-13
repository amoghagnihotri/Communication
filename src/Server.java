// Amogh Agnihotri
// 7/12/17
// Testing for BartenderBot display

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    //constructor
    public Server(){
        super("Display");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }

    //setup and run server
    public void startRunning(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){
                try{
                    //connect and communicate
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eof){
                    showMessage("\n Server ended the connection!");
                }finally{
                    close();
                }
            }
        } catch(IOException io){
            io.printStackTrace();
        }
    }

    //wait for connection, then display connection info
    private void waitForConnection() throws IOException{
        showMessage("Waiting to connect... \n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }

    //get stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage(" \n Streams are now setup! \n");
    }

    //during the communication
    private void whileChatting() throws IOException{
        String message = " You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n " + message);
            }catch(ClassNotFoundException classNotFound){
                showMessage("\n I can't understand that! ");
            }
        }while(!message.equals("CLIENT - END"));
    }

    //close streams and sockets after finished
    private void close(){
        showMessage("\n Closing connections... \n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException io){
            io.printStackTrace();
        }
    }

    //send message to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        }catch(IOException io){
            chatWindow.append("\n Can't send message ");
        }
    }

    //updates chat window
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(text);

                    }
                }
        );
    }

    //let the user type stuff
    private void ableToType(final Boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }

}
