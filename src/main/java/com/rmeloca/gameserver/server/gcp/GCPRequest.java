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
    private final int game;
    private final String op;
    private Object data;

    public GCPRequest(String id, int game, GCPOperation op, Object data) {
        this.id = id;
        this.game = game;
        this.op = op.toString();
        this.data = data;
    }

    public String getId() {
        return id;
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

}
