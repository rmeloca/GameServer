/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.synchronizer;

import com.rmeloca.gameserver.server.Synchronizer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class KeepAliveOnesWorker implements Runnable {

    private final Synchronizer synchronizer;

    public KeepAliveOnesWorker(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Friend> friends = synchronizer.getFriends();
                for (int i = 0; i < friends.size(); i++) {
                    Friend friend = friends.get(i);
                    if (friend.isOldfriendship()) {
                        friends.remove(i);
                    }
                }
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KeepAliveOnesWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
