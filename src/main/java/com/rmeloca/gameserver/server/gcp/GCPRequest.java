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
    private final String op;
    private Object data;

    public GCPRequest(String station, GCPOperation op, Object data) {
        this.id = station;
        this.op = op.toString();
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public GCPOperation getOperation() {
        return GCPOperation.valueOf(op.toUpperCase().replace("-", "_"));
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
