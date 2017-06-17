/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.http;

/**
 *
 * @author romulo
 */
public enum HTTPCode {
    OK(200),
    NOT_FOUND(404);

    private final int value;

    private HTTPCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
