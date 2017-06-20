package com.rmeloca.gameserver.server.synchronizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class HeartbeatWorker implements Runnable {

    private final MulticastSocket multicastSocket;

    public HeartbeatWorker(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[0];
                DatagramPacket heartbeatDatagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(Synchronizer.MULTICAST_IP), this.multicastSocket.getLocalPort());
                multicastSocket.send(heartbeatDatagramPacket);
                Thread.sleep(10000);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(HeartbeatWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
