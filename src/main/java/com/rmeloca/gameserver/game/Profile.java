/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author romulo
 */
public class Profile implements Serializable {

    private final String id;
    private int score;
    private final ArrayList<Trophy> trophies;

    public Profile(String id) {
        this.id = id;
        this.trophies = new ArrayList<>();
        this.score = 0;
    }

    public String getName() {
        return this.id;
    }

    public ArrayList<Trophy> getTrophies() {
        return this.trophies;
    }

    public ArrayList<String> getTrophiesName() {
        ArrayList<String> trohpiesName = new ArrayList<>();
        for (Trophy trophy : getTrophies()) {
            trohpiesName.add(trophy.getName());
        }
        return trohpiesName;
    }

    public void addTrophy(Trophy trophy) {
        if (!getTrophies().contains(trophy)) {
            getTrophies().add(trophy);
        }
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

    public void addScore(int score) {
        if (score > 0) {
            this.score += score;
        }
    }

}
