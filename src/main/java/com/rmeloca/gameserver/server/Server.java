/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.rmeloca.gameserver.controller.GameController;
import com.rmeloca.gameserver.controller.exception.ItemAlreadyExistException;
import com.rmeloca.gameserver.game.Game;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class Server {

    public static final String RESOURCES_PATH = System.getenv("RESOURCES_PATH") != null ? System.getenv("RESOURCES_PATH") : "./resources/";
    public static int PORT;

    public static void main(String argv[]) {
        if (argv.length < 1) {
            Server.PORT = 8080;
        } else {
            Server.PORT = Integer.parseInt(argv[0]);
        }

        GameController gameController = new GameController();
        Game game = new Game(1);
        try {
            gameController.add(game);
        } catch (ItemAlreadyExistException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Server is Running");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Server.PORT);
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                Socket socket = serverSocket.accept();
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "{0} conectou-se", socket.getInetAddress());
                Worker worker = new Worker(socket);
                pool.execute(worker);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
