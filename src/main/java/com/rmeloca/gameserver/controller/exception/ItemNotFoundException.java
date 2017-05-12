/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.controller.exception;

/**
 *
 * @author romulo
 */
public class ItemNotFoundException extends Exception {

    public ItemNotFoundException() {
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

}
