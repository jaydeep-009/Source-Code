package com.distributedwhiteboard.iface;

/*
Interface declares methods required for RMI messaging about shape details.
 */

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface IWhiteboardShape extends Remote {

    /**
     * Method returns shape type of whiteboard shape.
     * @return Shape type.
     * @throws RemoteException
     */
    public Shape getShape() throws RemoteException;

    /**
     * Method returns colour of whiteboard shape.
     * @return Colour.
     * @throws RemoteException
     */
    public Color getColour() throws RemoteException;

    /**
     * Method returns if a whiteboard shape is filled.
     * @return True if filled.
     * @throws RemoteException
     */
    public Boolean getFilled() throws RemoteException;
}