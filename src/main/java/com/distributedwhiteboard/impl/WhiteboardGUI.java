package com.distributedwhiteboard.impl;

/*
Class hosts the whiteboard GUI.
 */

import com.distributedwhiteboard.constants.Shapes;
import com.distributedwhiteboard.gui.WhiteboardJPanel;
import com.distributedwhiteboard.iface.IWhiteboardShape;
import com.distributedwhiteboard.iface.IWhiteboardUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class WhiteboardGUI {

    IWhiteboardUser user;
    private JPanel boardContainer;
    private JPanel panel1;
    private JList<String> clientList;
    private JButton rectangleButton;
    private JButton ellipseButton;
    private JButton lineButton;
    private JButton colourButton;
    private Color light_gray = new Color(242, 242, 242);
    private Color dark_gray = new Color(193, 193, 193);
    private JButton textButton;
    private JButton filledButton;
    private JButton eraserButton;
    public JToolBar toolBar;
    private JButton clearButton;
    private JButton roundButton;
    private JButton starButton;
    private WhiteboardJPanel whiteboardJPanel;
    public JFrame jFrame;

    /**
     * Constructor.
     *
     * @param user User that hosts GUI.
     * @throws RemoteException
     */
    public WhiteboardGUI(IWhiteboardUser user) throws RemoteException {
        this.jFrame = new JFrame();
        this.user = user;

        this.user.setGUI(this);

        // Update user list.
        this.updateUserList(user.getMessageController().getUsernames());

        // Start GUI.
        startGUI();
    }

    /**
     * Method starts GUI.
     *
     * @throws RemoteException
     */
    private void startGUI() throws RemoteException {

        whiteboardJPanel = new WhiteboardJPanel(this.user, this.user.getMessageController().getAllShapes(), this);
        boardContainer.add(whiteboardJPanel);

        jFrame.add(panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setSize(1200, 800);

        // Add button action listeners.
        rectangleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setCurrShape(Shapes.RECTANGLE);
            }
        });

        ellipseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setCurrShape(Shapes.ELLIPSE);
            }
        });

        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setCurrShape(Shapes.LINE);
            }
        });

        colourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setCurrColour();
            }
        });

        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String text = JOptionPane.showInputDialog("Please input text: ");
                whiteboardJPanel.setCurrText(text);
                whiteboardJPanel.setCurrShape(Shapes.TEXT);
            }
        });

        filledButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setFill();

                // Toggle colour.
                if (filledButton.getBackground() == light_gray) {
                    filledButton.setBackground(dark_gray);
                } else {
                    filledButton.setBackground(light_gray);
                }
            }
        });

        filledButton.setBackground(light_gray);

        eraserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.eraser();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    clearWhiteboard();
                } catch (RemoteException e) {
                    // Unable to clear whiteboard.
                    JOptionPane.showMessageDialog(null, "Unable to clear whiteboard.");
                }
            }
        });

        roundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setCurrShape(Shapes.ROUNDRECT);
            }
        });

        starButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                whiteboardJPanel.setCurrShape(Shapes.STAR);
            }
        });
    }

    /**
     * Method requests server to clear whiteboard.
     *
     * @throws RemoteException
     */
    private void clearWhiteboard() throws RemoteException {
        this.user.getMessageController().clearWhiteboard();
    }

    /**
     * Method adds a component to the toolbar.
     *
     * @param c
     */
    public void addTools(Component c) {
        this.toolBar.add(c);
    }

    /**
     * Method is called to receive a shape to the whiteboard.
     *
     * @param shape Shape submitted.
     */
    public void receiveShape(IWhiteboardShape shape) {
        whiteboardJPanel.addShape(shape);
    }

    /**
     * Method updates current user list.
     *
     * @param clientNames List of usernames
     */
    public void updateUserList(ArrayList<String> clientNames) {
        DefaultComboBoxModel<String> clientModel = new DefaultComboBoxModel<String>();
        for (String name : clientNames) {
            clientModel.addElement(name);
        }
        this.clientList.setModel(clientModel);
    }

    /**
     * Method calls user message controller to request all shapes in the whiteboard.
     */
    public void resyncShapes() {
        try {
            whiteboardJPanel.updateShapes(this.user.getMessageController().getAllShapes());
        } catch (RemoteException err) {
            JOptionPane.showMessageDialog(null, "An error has occurred" +
                    " receiving data from the server.");
        }
    }
}