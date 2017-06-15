/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.gcp;

/**
 *
 * @author romulo
 */
public enum GCPOperation {
    ADD_PROFILE, QUERY_PROFILE, ADD_GAME, ADD_TROPHY, GET_TROPHY, LIST_TROPHY, CLEAR_TROPHY, SAVE_STATE, LOAD_STATE, SAVE_MEDIA, LIST_MEDIA;

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
    }

}
