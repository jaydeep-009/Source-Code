package com.distributedwhiteboard.utils;

/*
Class acts as sentinel for user connectivity.
 */

import com.distributedwhiteboard.iface.IWhiteboardUser;

import java.rmi.RemoteException;
import java.util.Iterator;

public class UserSentinel implements Runnable{

    private WhiteboardUserHelper manager;

    public UserSentinel(WhiteboardUserHelper manager){
        this.manager = manager;
    }

    /**
     * Method checks all users each second.
     */
    public void run() {

        while(true) {
            if(manager.hasUsers()) {
                for (Iterator<IWhiteboardUser> iterator = this.manager.iterator(); iterator.hasNext();) {
                    IWhiteboardUser user = iterator.next();
                    try {
                        // Check on user.
                        user.pingUser();
                    } catch (RemoteException e) {
                        // User has disconnected.
                        manager.removeClient(user);
                    }
                }
            } else {
                continue;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }
}