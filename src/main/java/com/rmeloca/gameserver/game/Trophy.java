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
public class Trophy implements Serializable {

    private final String name;
    private int experience;
    private String title;
    private String description;

    public Trophy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getExperience() {
        return experience;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setTittle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        String toString = "{\"name\":" + this.name + "}";
        return toString;
    }
}
