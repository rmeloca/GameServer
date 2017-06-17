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
public class GCPResponse {

    private final GCPCode response;
    private final Object data;

    public GCPResponse(GCPCode response) {
        this.response = response;
        this.data = "";
    }

    public GCPResponse(GCPCode response, Object data) {
        this.response = response;
        this.data = data;
    }

    public GCPCode getCode() {
        return response;
    }

}
