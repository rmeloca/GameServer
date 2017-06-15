/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.rmeloca.gameserver.server.synchronizer.HeartbeatWorker;
import com.rmeloca.gameserver.server.synchronizer.StethoscopeWorker;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class Synchronizer implements Runnable {

    private static final int MULTICAST_PORT = 8889;
    private static final String MULTICAST_IP = "255.1.2.3";
    private final Map<InetAddress, Long> friendList;
    private final StethoscopeWorker stethoscopeWorker;
    private final HeartbeatWorker heartbeatWorker;

    public Synchronizer() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_IP));
            stethoscopeWorker = new StethoscopeWorker(multicastSocket, this);
            heartbeatWorker = new HeartbeatWorker(multicastSocket);
        } catch (IOException ex) {
            Logger.getLogger(Synchronizer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
        this.friendList = new HashMap<>();
    }

    @Override
    public void run() {
        Thread stethoscopeThread = new Thread(this.stethoscopeWorker);
        Thread heartbeatThread = new Thread(this.heartbeatWorker);
        heartbeatThread.start();
        stethoscopeThread.start();
    }

    public void addFriend(InetAddress inetAddress) {
        this.friendList.put(inetAddress, System.currentTimeMillis());
    }
}
