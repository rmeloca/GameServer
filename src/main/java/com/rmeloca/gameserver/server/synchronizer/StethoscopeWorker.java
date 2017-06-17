package com.rmeloca.gameserver.server.synchronizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.rmeloca.gameserver.server.Synchronizer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class StethoscopeWorker implements Runnable {

    private final MulticastSocket multicastSocket;
    private final Synchronizer synchronizer;

    public StethoscopeWorker(MulticastSocket multicastSocket, Synchronizer synchronizer) {
        this.multicastSocket = multicastSocket;
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[0];
                DatagramPacket heartbeatDatagramPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(heartbeatDatagramPacket);
                if (!isMine(heartbeatDatagramPacket)) {
                    InetAddress address = heartbeatDatagramPacket.getAddress();
                    this.synchronizer.meetFriend(new Friend(address, System.currentTimeMillis()));
                }
            } catch (IOException ex) {
                Logger.getLogger(HeartbeatWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isMine(DatagramPacket heartbeatDatagramPacket) throws SocketException {
        InetAddress address = heartbeatDatagramPacket.getAddress();
        NetworkInterface friendNetwork = NetworkInterface.getByInetAddress(address);
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface nextElement = networkInterfaces.nextElement();
            if (friendNetwork.equals(nextElement)) {
                return true;
            }
        }
        return false;
    }
}
