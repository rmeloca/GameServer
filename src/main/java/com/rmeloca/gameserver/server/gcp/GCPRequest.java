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

    private final String station;
    private final GCPOperation operation;
    private final String data;

    public GCPRequest(String station, GCPOperation operation, String data) {
        this.station = station;
        this.operation = operation;
        this.data = data;
    }

    public String getStation() {
        return station;
    }

    public GCPOperation getOperation() {
        return operation;
    }

    public String getData() {
        return data;
    }

}
