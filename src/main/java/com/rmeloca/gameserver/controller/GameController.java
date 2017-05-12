/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.controller;

import com.rmeloca.gameserver.game.Game;
import com.rmeloca.gameserver.persistence.GamePersistence;

/**
 *
 * @author romulo
 */
public class GameController extends Controller<Game> {

    public GameController() {
        super(new GamePersistence());
    }

}
