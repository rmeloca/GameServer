/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.synchronizer;

import com.rmeloca.gameserver.server.GameServer;
import com.rmeloca.gameserver.server.gcp.GCPOperation;
import com.rmeloca.gameserver.server.gcp.GCPRequest;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author romulo
 */
public class FriendTest {

    public FriendTest() {
    }

    @Test
    public void testSomeMethod() {
        GameServer.GAME_SERVER_PORT = 8888;
        try {
            InetAddress address = InetAddress.getLocalHost();
            Friend friend = new Friend(address, System.currentTimeMillis());
            friend.ask(new GCPRequest("rmeloca", "marioevolution", GCPOperation.ADD_PROFILE, null));
        } catch (MalformedURLException ex) {
            Logger.getLogger(FriendTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(FriendTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
