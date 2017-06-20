/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.Protocol;

import com.google.gson.Gson;
import com.rmeloca.gameserver.game.Profile;
import com.rmeloca.gameserver.game.Trophy;
import com.rmeloca.gameserver.server.gcp.GCPOperation;
import com.rmeloca.gameserver.server.gcp.GCPRequest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author romulo
 */
public class RequestTest {

    public RequestTest() {
    }

    @Test
    public void testSomeMethod() {
        Profile profile = new Profile("leonardo");
        Trophy trophy = new Trophy("first-dead");
        trophy.setDescription("first-dead");
        trophy.setExperience(2);
        trophy.setTittle("first-dead");
        String toString = trophy.toString();
        GCPRequest request = new GCPRequest("leonardo", "marioevolution", GCPOperation.ADD_TROPHY, toString);
        Gson gson = new Gson();
        String toJson = gson.toJson(request);
        System.out.println(toJson);
    }

}
