/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author romulo
 */
public class HTTPHeader implements Iterable<Map.Entry<String, List<String>>> {

    private final Map<String, List<String>> attributes;

    public HTTPHeader() {
        this.attributes = new HashMap<>();
    }

    public void addAttribute(String key, List<String> value) {
        this.attributes.put(key, value);
    }

    public void addAttribute(String key, String... value) {
        addAttribute(key, Arrays.asList(value));
    }

    public boolean containsProperty(String property) {
        return this.attributes.containsKey(property);
    }

    public List<String> getProperty(String property) {
        return this.attributes.get(property);
    }

    @Override
    public Iterator<Map.Entry<String, List<String>>> iterator() {
        return this.attributes.entrySet().iterator();
    }

}
