/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author romulo
 */
public class HTTPParameters implements Iterable<Map.Entry<String, String>> {

    private final Map<String, String> attributes;

    public HTTPParameters() {
        this.attributes = new HashMap<>();
    }

    public HTTPParameters(String parameters) {
        this.attributes = new HashMap<>();
        String[] split = parameters.split("&");
        for (String string : split) {
            String[] keyValue = string.split("=");
            this.attributes.put(keyValue[0], keyValue[1]);
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return attributes.entrySet().iterator();
    }

}
