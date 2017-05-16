/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.google.gson.Gson;
import com.rmeloca.gameserver.server.gcp.GCPCode;
import com.rmeloca.gameserver.server.gcp.GCPOperation;
import com.rmeloca.gameserver.server.gcp.GCPRequest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author romulo
 */
public class GameHandlerTest {

    public GameHandlerTest() {
    }

    @Test
    public void testSomeMethod() {
        String json = "{\"station\":\"john_doe\",\"operation\":\"ADD_TROPHY\", \"data\":\"\"}";
        Gson gson = new Gson();
        GCPRequest gcpRequest = gson.fromJson(json, GCPRequest.class);
        GCPOperation operation = gcpRequest.getOperation();
        assertEquals(operation, GCPOperation.ADD_TROPHY);
    }

}
