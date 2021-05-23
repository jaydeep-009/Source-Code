package com.distributedwhiteboard;

/*
Class for user in whiteboard server.
 */

import com.distributedwhiteboard.constants.Result;
import com.distributedwhiteboard.iface.IMessageController;
import com.distributedwhiteboard.iface.IWhiteboardManager;
import com.distributedwhiteboard.iface.IWhiteboardShape;
import com.distributedwhiteboard.iface.IWhiteboardUser;
import com.distributedwhiteboard.impl.WhiteboardGUI;
import com.distributedwhiteboard.utils.Utils;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class JoinWhiteBoard extends UnicastRemoteObject implements IWhiteboardUser, Remote {

	private static final long serialVersionUID = -2956778517254700259L;
	private IMessageController messageController;
    private static String userName = "";
    private WhiteboardGUI whiteboardGUI = null;

    public static void main(String args[]) throws RemoteException, MalformedURLException, NotBoundException, ServerNotActiveException {

        if (args.length != 3) {
            JOptionPane.showMessageDialog(null, "Incorrect command line arguments.\n" +
                    "Run as \"java -jar WhiteboardUser.jar <serverIP> <serverPort> <userName>");
            System.exit(0);
        }

        String ip = args[0];
        userName = args[2];
        Integer port = -1;

        // Assert correct port number.
        try{
            port = Integer.parseInt(args[1]);
            Utils.verifyPort(port);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Invalid port number '" + port +"'.");
            System.exit(0);
        }

		/*
		 * String userName =
		 * JOptionPane.showInputDialog("Please choose your username: ");
		 * 
		 * if (userName == null || userName.equals("")) {
		 * JOptionPane.showMessageDialog(null,
		 * "No username given. Application shutting down."); System.exit(0); }
		 */
        
        try {
            // Connect to server.
            String serverName = "//" + ip + ":" + port.toString() + "/Server";
            IWhiteboardManager server = (IWhiteboardManager) Naming.lookup(serverName);
            IWhiteboardUser client = new JoinWhiteBoard(server, userName);

            // Run GUI in separate thread.
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        WhiteboardGUI gui = new WhiteboardGUI(client);
                        gui.toolBar.add(Box.createHorizontalGlue());
                        gui.addTools(new JLabel("Address: " + serverName + "    "));
                    } catch (RemoteException e) {
                        JOptionPane.showMessageDialog(null, "Unable to start GUI.");
                        System.exit(0);
                    } catch (Exception e){
                        JOptionPane.showMessageDialog(null, "Unable to start GUI.");
                        System.exit(0);
                    }
                }
            });
            t.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
        }
    }

    /**
     * Constructor.
     * @param server Server to connect to.
     * @param userName Username.
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public JoinWhiteBoard(IWhiteboardManager server, String userName) throws RemoteException, ServerNotActiveException {
        this.userName = userName;

        if (this.userName == null || this.userName.equals("")) {
            Random randomCode = new Random(this.userName.hashCode());
            this.userName = "user-" + randomCode.nextInt(1000);
        }

        try {
            Result response = server.requestRegister(this);
            switch (response) {
                case REJECT:
                    JOptionPane.showMessageDialog(null, "The whiteboard manager rejected your join request. Application shutting down.");
                    System.exit(0);
                    break;

                case DUPLICATE:
                    JOptionPane.showMessageDialog(null, "The username " + this.userName + " is taken. Application shutting down.");
                    System.exit(0);
                    break;

                default:
                    this.messageController = server.registerUser(this);
                    break;
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
            System.exit(0);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
            System.exit(0);
        }
    }

    /**
     * Getter for user's message controller.
     *
     * @return Message contoller instance.
     * @throws RemoteException
     */
    public IMessageController getMessageController() {
        return this.messageController;
    }

    /**
     * Method used to request username.
     *
     * @return Username.
     * @throws RemoteException
     */
    public String getName() {
        return this.userName;
    }

    /**
     * Method used to add a shape to the whiteboard.
     *
     * @param shape Whiteboard shape to add.
     * @throws RemoteException
     */
    public void receiveShape(IWhiteboardShape shape) throws RemoteException {
        if (this.whiteboardGUI != null) this.whiteboardGUI.receiveShape(shape);
    }

    /**
     * Method used to refresh shapes in whiteboard.
     *
     * @throws RemoteException
     */
    public void refreshShapes() throws RemoteException {
        if (this.whiteboardGUI != null) this.whiteboardGUI.resyncShapes();
    }

    /**
     * Method used to refresh username list.
     *
     * @throws RemoteException
     */
    public void refreshUsernames() throws RemoteException {
        if (this.whiteboardGUI != null) this.whiteboardGUI.updateUserList(this.getMessageController().getUsernames());
    }

    /**
     * Method used to kick a user from the server.
     *
     * @param message Message from server.
     * @throws RemoteException
     */
    public void kick(String message) throws RemoteException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
                System.exit(0);
            }
        });
        t.start();
    }

    /**
     * Method used to set the GUI for a user.
     *
     * @param gui GUI to set.
     * @throws RemoteException
     */
    public void setGUI(WhiteboardGUI gui) throws RemoteException {
        this.whiteboardGUI = gui;
    }

    /**
     * Method used to test if client is still connected.
     *
     * @return Dummy value to indicate alive.
     * @throws RemoteException
     */
    public Boolean pingUser() {
        return true;
    }

}