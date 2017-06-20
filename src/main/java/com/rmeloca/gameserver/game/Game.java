/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.game;

import com.rmeloca.gameserver.controller.exception.ItemNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author romulo
 */
public class Game implements Serializable {

    private final String id;
    private final Collection<Profile> profiles;

    public Game(String id) {
        if (id == null) {
            throw new RuntimeException();
        }
        this.id = id;
        this.profiles = new ArrayList<>();
    }

    public Collection<Profile> getProfiles() {
        return profiles;
    }

    public void addProfile(Profile profile) {
        if (!getProfiles().contains(profile)) {
            getProfiles().add(profile);
        }
    }

    public void deleteProfile(Profile profile) {
        if (getProfiles().contains(profile)) {
            getProfiles().remove(profile);
        }
    }

    public void updateProfile(Profile profile) {
        deleteProfile(profile);
        addProfile(profile);
    }

    public String getId() {
        return id;
    }

    public Profile getProfile(Profile profile) throws ItemNotFoundException {
        for (Profile compare : getProfiles()) {
            if (compare.equals(profile)) {
                return compare;
            }
        }
        throw new ItemNotFoundException();
    }

    public String getProfilesJSON() {
        StringBuilder dump = new StringBuilder();
        dump.append("[");
        for (Profile profile : getProfiles()) {
            dump.append(profile.toString());
        }
        dump.append("]");
        return dump.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Game)) {
            return false;
        }
        Game other = (Game) obj;
        return other.getId().equals(getId());
    }

    @Override
    public String toString() {
        StringBuilder dump = new StringBuilder();
        dump.append("{");
        dump.append("\"profiles\"").append(":");
        dump.append(getProfilesJSON());
        dump.append("}");
        return dump.toString();
    }

}
