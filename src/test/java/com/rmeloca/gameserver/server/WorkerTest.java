/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.rmeloca.gameserver.server.http.HTTPCode;
import com.rmeloca.gameserver.server.http.HTTPHeader;
import com.rmeloca.gameserver.server.http.HTTPResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.Test;

/**
 *
 * @author romulo
 */
public class WorkerTest {

    public WorkerTest() {
    }

    @Test
    public void testSomeMethod() {
        try {
            URL url = new URL("http://localhost:8080");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                HTTPResponse hTTPResponse = new HTTPResponse("GET", HTTPCode.OK, "message", "leonardo".getBytes(), new HTTPHeader());
                hTTPResponse.send(outputStream);
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
            Logger.getLogger(WorkerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WorkerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
