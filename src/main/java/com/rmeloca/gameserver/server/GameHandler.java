/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.rmeloca.gameserver.server.synchronizer.Synchronizer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
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

    protected GCPResponse getGameResource(String path) {
        Object data = "";
        GCPCode code = GCPCode.FAIL;

        GameController gameController = new GameController();
        Game game = null;
        String[] url = path.split("/");
        if (url.length >= 2) {
            try {
                String gameID = url[2];
                game = new Game(gameID);
                game = gameController.get(game);
                data = game;
                code = GCPCode.OK;
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
                return new GCPResponse(code, data);
            }
        }
        if (url.length >= 3) {
            data = game.getProfiles();
            code = GCPCode.OK;
        }
        if (url.length >= 4) {
            try {
                String profileID = url[4];
                Profile profile = new Profile(profileID);
                data = game.getProfile(profile);
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
                code = GCPCode.FAIL;
                data = "";
                return new GCPResponse(code, data);
            }
        }
        return new GCPResponse(code, data);
    }

    protected GCPResponse postGameResource(HTTPRequest request, String path) {
        try {
            Gson gson = new Gson();
            GCPRequest gcpRequest = gson.fromJson(request.getContent(), GCPRequest.class);
            GCPOperation operation = gcpRequest.getOperation();
            GCPResponse gcpResponse = null;

            String gameID = gcpRequest.getGameID();
            String profileID = gcpRequest.getID();
            Game game = new Game(gameID);
            Profile profile = new Profile(profileID);
            GameController gameController = new GameController();
            try {
                game = gameController.get(game);
            } catch (ItemNotFoundException ex) {
            }
            try {
                profile = game.getProfile(profile);
            } catch (ItemNotFoundException ex) {
                if (!operation.equals(GCPOperation.ADD_PROFILE) && gcpRequest.isClient()) {
                    Synchronizer synchronizer = GameServer.getSynchronizer();
                    gcpResponse = synchronizer.askToFriends(gcpRequest);
                    return gcpResponse;
                }
            }

            Trophy trophy;
            switch (operation) {
                case ADD_TROPHY:
                    JsonObject objectTrophy = gson.fromJson(request.getContent(), JsonObject.class).get("data").getAsJsonObject();
                    trophy = gson.fromJson(objectTrophy, Trophy.class);
                    profile.addTrophy(trophy);
                    gcpResponse = new GCPResponse(GCPCode.OK, trophy.getName());
                    break;
                case LIST_TROPHY:
                    ArrayList<String> trophiesName = profile.getTrophiesName();
                    gcpResponse = new GCPResponse(GCPCode.OK, trophiesName);
                    break;
                case CLEAR_TROPHY:
                    profile.clearTrophy();
                    gcpResponse = new GCPResponse(GCPCode.OK);
                    break;
                case ADD_PROFILE:
                    try {
                        gameController.get(game);
                    } catch (ItemNotFoundException ex) {
                        try {
                            gameController.add(game);
                        } catch (ItemAlreadyExistException ex1) {
                            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    } finally {
                        game.addProfile(profile);
                        gcpResponse = new GCPResponse(GCPCode.OK);
                    }
                    break;
                case QUERY_PROFILE:
                    gcpResponse = new GCPResponse(GCPCode.OK, profile);
                    break;
                case ADD_GAME:
                    try {
                        gameController.add(game);
                        gcpResponse = new GCPResponse(GCPCode.OK);
                    } catch (ItemAlreadyExistException ex) {
                        gcpResponse = new GCPResponse(GCPCode.FAIL);
                    }
                    break;
                case GET_TROPHY:
                    trophy = new Trophy(gcpRequest.getData().toString());
                    try {
                        trophy = profile.getTrophy(trophy);
                        gcpResponse = new GCPResponse(GCPCode.OK, trophy);
                    } catch (ItemNotFoundException ex) {
                        gcpResponse = new GCPResponse(GCPCode.FAIL);
                    }
                    break;
                case SAVE_STATE:
                    LinkedTreeMap data = (LinkedTreeMap) gcpRequest.getData();
                    Double score = (Double) data.get("score");
                    profile.addScore(score.intValue());
                    gcpResponse = new GCPResponse(GCPCode.OK);
                    break;
                case LOAD_STATE:
                    gcpResponse = new GCPResponse(GCPCode.OK, profile);
                    break;
                case SAVE_MEDIA:
                    break;
                case LIST_MEDIA:
                    gcpResponse = new GCPResponse(GCPCode.OK, profile.getScreenshots());
                    break;
                default:
                    throw new AssertionError(operation.name());
            }
            game.updateProfile(profile);
            gameController.update(game);
            return gcpResponse;
        } catch (Exception ex) {
            return new GCPResponse(GCPCode.FAIL);
        }
    }
}
