/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class ServerTest {

    public ServerTest() {
    }

    public void testSomeMethod() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            int read;
            while (true) {
                while (true) {
                    read = inputStream.read();
                    if (read == 13) {
                        inputStream.read();
                        break;
                    }
                    stringBuilder.append((char) read);
                }
                System.out.println(stringBuilder);
                stringBuilder = new StringBuilder();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String readStartLine(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int read = inputStream.read();
            if (read == 13) {
                inputStream.read();
                break;
            }
            stringBuilder.append((char) read);
        }
        return stringBuilder.toString();
    }

    private String readMessageHeader(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            while (true) {
                int read = inputStream.read();
                if (read == 13) {
                    inputStream.read();
                    break;
                }
                stringBuilder.append((char) read);
            }
            break;
        }
        return stringBuilder.toString();
    }
}
