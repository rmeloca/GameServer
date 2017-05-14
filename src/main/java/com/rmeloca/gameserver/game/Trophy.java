/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.game;

/**
 *
 * @author romulo
 */
public class Trophy {

    private final String name;
    private int experience;
    private String tittle;
    private String description;

    public Trophy(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    @Override
    public String toString() {
        String toString = "{\"name\":" + this.name + "}";
        return toString;
    }
}
