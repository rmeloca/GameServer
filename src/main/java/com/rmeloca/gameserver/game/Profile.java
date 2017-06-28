/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.game;

import com.rmeloca.gameserver.controller.exception.ItemNotFoundException;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author romulo
 */
public class Profile implements Serializable {

    private final String id;
    private String password;
    private int score;
    private int level;
    private int lifes;
    private Coordinate coordinate;
    private final ArrayList<Trophy> trophies;
    private final ArrayList<Image> screenshots;

    public Profile(String id) {
        this.id = id;
        this.trophies = new ArrayList<>();
        this.score = 0;
        this.level = 0;
        this.screenshots = new ArrayList<>();
        this.password = "";
        this.coordinate = new Coordinate(0.0, 0.0);
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

    public void levelUp() {
        this.level++;
    }

    public void addScore(int score) {
        if (score > 0) {
            this.score += score;
        }
    }

    public void clearTrophy() {
        this.trophies.clear();
    }

    public ArrayList<Image> getScreenshots() {
        return screenshots;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Trophy getTrophy(Trophy trophy) throws ItemNotFoundException {
        for (Trophy item : trophies) {
            if (item.equals(trophy)) {
                return item;
            }
        }
        throw new ItemNotFoundException();
    }

    public boolean isAuthentic(Profile givenProfile) {
        return this.equals(givenProfile) && this.password.equals(givenProfile.password);
    }
}
