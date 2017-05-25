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
    ADD_TROPHY, LIST_TROPHY, CLEAR_TROPHY, ADD_PLAYER, ADD_SCORE;

    @Override
    public String toString() {
        switch (this) {
            case ADD_SCORE:
                return "add-score";
            case ADD_TROPHY:
                return "add-trophy";
            case LIST_TROPHY:
                return "list-trophy";
            case CLEAR_TROPHY:
                return "clear-trophy";
            case ADD_PLAYER:
                return "add-player";
            default:
                throw new AssertionError(this.name());
        }
    }

}
