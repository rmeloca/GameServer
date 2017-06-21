/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.gcp;

/**
 *
 * @author romulo
 */
public class GCPRequest {

    private final String id;
    private final String game;
    private final String op;
    private final String src;
    private Object data;

    public GCPRequest(String id, String game, GCPOperation op, Object data) {
        this.id = id;
        this.game = game;
        this.op = op.toString();
        this.src = "meloca";
        this.data = data;
    }

    public String getID() {
        return id;
    }

    public String getGameID() {
        return game;
    }

    public GCPOperation getOperation() {
        String operation = op.toUpperCase().replace("-", "_");
        return GCPOperation.valueOf(operation);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isServer() {
        return src != null && !src.isEmpty();
    }

    public boolean isClient() {
        return !isServer();
    }

}
