/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.game;

import java.io.Serializable;

/**
 *
 * @author romulo
 */
public class Profile implements Serializable {

    private final String name;

    public Profile(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Profile)) {
            return false;
        }
        Profile other = (Profile) obj;
        return other.getName().equals(getName());
    }

    @Override
    public String toString() {
        StringBuilder dump = new StringBuilder();
        dump.append("{");
        dump.append("\"name\"").append(":");
        dump.append("\"").append(getName()).append("\"");
        dump.append("}");
        return dump.toString();
    }

}
