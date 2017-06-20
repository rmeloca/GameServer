/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.controller;

import com.rmeloca.gameserver.controller.exception.ItemNotFoundException;
import com.rmeloca.gameserver.game.Game;
import com.rmeloca.gameserver.game.Profile;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author romulo
 */
public class GameControllerTest {

    public GameControllerTest() {
    }

    @Test
    public void testSomeMethod() {
        GameController gameController = new GameController();
        try {
            Game get = gameController.get(new Game("marioevolution"));
            Profile profile = new Profile("leonardo");
            get.addProfile(profile);
            gameController.update(get);
        } catch (ItemNotFoundException ex) {
            Logger.getLogger(GameControllerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collection<Game> items = gameController.getItems();
        for (Game item : items) {
            System.out.println(item);
        }
    }

}
