/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.rmeloca.gameserver.server.gcp.GCPCode;
import com.rmeloca.gameserver.server.gcp.GCPRequest;
import com.rmeloca.gameserver.server.gcp.GCPResponse;
import com.rmeloca.gameserver.server.synchronizer.Friend;
import com.rmeloca.gameserver.server.synchronizer.HeartbeatWorker;
import com.rmeloca.gameserver.server.synchronizer.KeepAliveOnesWorker;
import com.rmeloca.gameserver.server.synchronizer.StethoscopeWorker;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class Synchronizer implements Runnable {

    public static final int MULTICAST_PORT = 8889;
    public static final String MULTICAST_IP = "225.1.2.3";
    private final List<Friend> friendList;
    private final StethoscopeWorker stethoscopeWorker;
    private final HeartbeatWorker heartbeatWorker;
    private final KeepAliveOnesWorker keepAliveOnesWorker;

    public Synchronizer() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(InetAddress.getByName(MULTICAST_IP));
            this.stethoscopeWorker = new StethoscopeWorker(multicastSocket, this);
            this.heartbeatWorker = new HeartbeatWorker(multicastSocket);
            this.keepAliveOnesWorker = new KeepAliveOnesWorker(this);
        } catch (IOException ex) {
            Logger.getLogger(Synchronizer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
        this.friendList = new ArrayList<>();
    }

    @Override
    public void run() {
        Thread stethoscopeThread = new Thread(this.stethoscopeWorker);
        Thread heartbeatThread = new Thread(this.heartbeatWorker);
        Thread keepAliveOnesThread = new Thread(this.keepAliveOnesWorker);
        heartbeatThread.start();
        stethoscopeThread.start();
        keepAliveOnesThread.start();
    }

    public void meetFriend(Friend friend) {
        for (Friend myFriend : this.friendList) {
            if (myFriend.equals(friend)) {
                myFriend.meet(friend.getLastMeeting());
                return;
            }
        }
        this.friendList.add(friend);
    }

    public List<Friend> getFriends() {
        return this.friendList;
    }

    public GCPResponse askToFriends(GCPRequest gcpRequest) {
        GCPResponse response = new GCPResponse(GCPCode.ERROR);
        for (Friend friend : friendList) {
            response = friend.ask(gcpRequest);
            if (response.getCode().equals(GCPCode.OK)) {
                return response;
            }
        }
        return response;
    }
}
