/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rmeloca.gameserver.controller.GameController;
import com.rmeloca.gameserver.controller.exception.ItemAlreadyExistException;
import com.rmeloca.gameserver.controller.exception.ItemNotFoundException;
import com.rmeloca.gameserver.game.Game;
import com.rmeloca.gameserver.game.Profile;
import com.rmeloca.gameserver.game.Trophy;
import com.rmeloca.gameserver.server.gcp.GCPCode;
import com.rmeloca.gameserver.server.gcp.GCPOperation;
import com.rmeloca.gameserver.server.gcp.GCPRequest;
import com.rmeloca.gameserver.server.gcp.GCPResponse;
import com.rmeloca.gameserver.server.http.HTTPRequest;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class GameHandler {

    private final Game game;

    public GameHandler() {
        Game game = new Game(1);
        GameController gameController = new GameController();
        try {
            game = gameController.get(game);
        } catch (ItemNotFoundException ex) {
            try {
                gameController.add(game);
            } catch (ItemAlreadyExistException ex1) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.game = game;
    }

    protected GCPResponse getGameResource(String path) {
        Object data = "";
        GCPCode code = GCPCode.OK;
        if (path.startsWith("/game/profiles")) {
            String[] url = path.split("/");
            if (url.length < 4) {
                data = game.getProfiles();
                code = GCPCode.OK;
            } else {
                String profileName = url[3];
                try {
                    data = game.getProfile(new Profile(profileName));
                    code = GCPCode.OK;
                } catch (ItemNotFoundException ex) {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (path.startsWith("/game")) {
            data = game;
            code = GCPCode.OK;
        }
        GCPResponse gcpResponse = new GCPResponse(code, data);
        return gcpResponse;
    }

    protected GCPResponse postGameResource(HTTPRequest request, String path) {
        Gson gson = new Gson();
        GCPRequest gcpRequest = gson.fromJson(request.getContent(), GCPRequest.class);
        String station = gcpRequest.getId();
        Profile profile;
        try {
            profile = game.getProfile(new Profile(station));
        } catch (ItemNotFoundException ex) {
            profile = new Profile(station);
            game.addProfile(profile);
            try {
                GameController gameController = new GameController();
                gameController.update(game);
            } catch (ItemNotFoundException ex1) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        GCPOperation operation = gcpRequest.getOperation();
        GCPResponse gcpResponse;
        switch (operation) {
            case ADD_SCORE:
                int score = gson.fromJson(request.getContent(), JsonObject.class).get("score").getAsInt();
                profile.addScore(score);
                gcpResponse = new GCPResponse(GCPCode.OK);
                break;
            case ADD_TROPHY:
                JsonObject objectTrophy = gson.fromJson(request.getContent(), JsonObject.class).get("data").getAsJsonObject();
                Trophy trophy = gson.fromJson(objectTrophy, Trophy.class);
                profile.addTrophy(trophy);
                gcpResponse = new GCPResponse(GCPCode.OK, trophy.getName());
                break;
            case LIST_TROPHY:
                ArrayList<String> trophiesName = profile.getTrophiesName();
                gcpResponse = new GCPResponse(GCPCode.OK, gson.toJson(trophiesName));
                break;
            case CLEAR_TROPHY:
                gcpResponse = new GCPResponse(GCPCode.OK);
                break;
            case ADD_PLAYER:
                gcpResponse = new GCPResponse(GCPCode.OK);
                break;
            default:
                throw new AssertionError(operation.name());
        }
        game.updateProfile(profile);

        try {
            GameController gameController = new GameController();
            gameController.update(game);
        } catch (ItemNotFoundException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return gcpResponse;
        }
    }
}
