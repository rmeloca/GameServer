/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.synchronizer;

import com.rmeloca.gameserver.server.GameServer;
import com.rmeloca.gameserver.server.gcp.GCPRequest;
import com.rmeloca.gameserver.server.gcp.GCPResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author romulo
 */
public class Friend {

    private long lastMeeting;
    private final URL address;

    public Friend(URL address, long meetingTime) {
        this.address = address;
        this.lastMeeting = meetingTime;
    }

    public Friend(InetAddress address, long meetingTime) throws MalformedURLException {
        this.address = new URL(address.getHostAddress());
        this.lastMeeting = meetingTime;
    }

    public void meet(long time) {
        this.lastMeeting = time;
    }

    public long getLastMeeting() {
        return this.lastMeeting;
    }

    public boolean isOldfriendship() {
        long currentTime = System.currentTimeMillis();
        long friendShiptime = currentTime - this.lastMeeting;
        return friendShiptime > 60000;
    }

    public GCPResponse ask(GCPRequest gcpRequest) {
        try {
            URL url = new URL(address.getHost() + ":" + GameServer.GAME_SERVER_PORT);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(1);
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Stream<String> lines = bufferedReader.lines();
                Iterator<String> iterator = lines.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    System.out.println(next);
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Friend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Friend.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Friend)) {
            return false;
        }
        Friend other = (Friend) obj;
        return this.address.equals(other.address);
    }

}
