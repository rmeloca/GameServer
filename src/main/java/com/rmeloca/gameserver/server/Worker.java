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
import com.rmeloca.gameserver.server.gcp.GCPCode;
import com.rmeloca.gameserver.server.gcp.GCPResponse;
import com.rmeloca.gameserver.server.http.HTTPCode;
import com.rmeloca.gameserver.server.http.HTTPHeader;
import com.rmeloca.gameserver.server.http.HTTPRequest;
import com.rmeloca.gameserver.server.http.HTTPResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romulo
 */
public class Worker implements Runnable {

    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.inputStream = this.clientSocket.getInputStream();
            this.outputStream = this.clientSocket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }

    @Override
    public void run() {
        try {
            HTTPRequest httpRequest = new HTTPRequest(this.inputStream);
            Logger.getLogger(Worker.class.getName()).log(Level.INFO, "{0} {1}", new Object[]{httpRequest.getMethod().name(), httpRequest.getResource()});
            String path;
            HTTPResponse HTTPResponse;
            switch (httpRequest.getMethod()) {
                case GET:
                    path = httpRequest.getResource();
                    if (path.contains(".html")) {
                        HTTPResponse = getFile(httpRequest, path, detectMimeType(path));
                    } else if (path.endsWith(".css")) {
                        HTTPResponse = getCSS(httpRequest, path);
                    } else if (path.endsWith(".json")) {
                        HTTPResponse = getFile(httpRequest, path, "application/json");
                    } else if (path.endsWith(".js")) {
                        HTTPResponse = getJS(httpRequest, path);
                    } else if (path.endsWith(".png")) {
                        HTTPResponse = getPNG(httpRequest, path);
                    } else if (path.endsWith(".ttf")) {
                        HTTPResponse = getOctet(httpRequest, path);
                    } else if (path.endsWith(".zip")) {
                        HTTPResponse = getOctet(httpRequest, path);
                    } else if (path.endsWith(".map")) {
                        HTTPResponse = getOctet(httpRequest, path);
                    } else if (path.endsWith(".txt")) {
                        HTTPResponse = getTXT(httpRequest, path);
                    } else if (path.startsWith("/files/")) {
                        path = path.replaceFirst("/files", "");
                        HTTPResponse = getJSON(httpRequest, path);
                    } else if (path.startsWith("/game")) {
                        Object responseJSON = getGameResource(path);
                        GCPResponse gcpResponse = new GCPResponse(GCPCode.OK, responseJSON);
                        HTTPResponse = getJSON(httpRequest, gcpResponse);
                    } else if (resourceExists(path)) {
                        path = "index.html";
                        HTTPResponse = getHTML(httpRequest, path);
                    } else {
                        path = "-";
                        HTTPResponse = getHTML(httpRequest, path);
                    }
                    break;
                case POST:
                    path = httpRequest.getResource();
                    if (path.startsWith("/game")) {
                        postGameResource(httpRequest, path);
                        HTTPResponse = getJSON(httpRequest, path);
                    } else {
                        path = "-";
                        HTTPResponse = getHTML(httpRequest, path);
                    }
                    break;
                default:
                    throw new AssertionError(httpRequest.getMethod().name());
            }
            HTTPResponse.send(this.outputStream);
            Logger.getLogger(Worker.class.getName()).log(Level.INFO, "{0} {1}", new Object[]{String.valueOf(HTTPResponse.getCode()), path});
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                this.inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Logger.getLogger(Worker.class.getName()).log(Level.INFO, "Connection Closed");
                this.clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getDateGTM() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        String dateGMT = simpleDateFormat.format(date) + " GMT";
        return dateGMT;
    }

    private String parseDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(date);
    }

    private HTTPResponse getFile(HTTPRequest request, String path, String type) throws IOException {
        HTTPCode code;
        String message;
        String protocol;
        byte[] content;

        File file = new File(Server.RESOURCES_PATH, path);
        if (file.exists()) {
            code = HTTPCode.OK;
            message = "OK";
        } else {
            code = HTTPCode.NOT_FOUND;
            message = "Not Found";
            type = "text/html";
            file = new File(Server.RESOURCES_PATH, "404.html");
        }
        protocol = request.getProtocol();
        content = Files.readAllBytes(file.toPath());

        String dateGMT = getDateGTM();

        HTTPHeader header = new HTTPHeader();
        header.addAttribute("Location", "http://localhost:8080/");
        header.addAttribute("Date", dateGMT);
        header.addAttribute("Server", "RMelocaServer/1.0");
        header.addAttribute("Content-Type", type);
        header.addAttribute("Content-Length", String.valueOf(content.length));

        HTTPResponse response = new HTTPResponse(protocol, code, message, content, header);
        return response;
    }

    private String detectMimeType(String url){
        if (url.contains(".html")) {
            return "text/html";
        }
        return "application/octet-stream";
    }
    
    @Deprecated
    private HTTPResponse getOctet(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, "application/octet-stream");
    }

    @Deprecated
    private HTTPResponse getHTML(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, "text/html");
    }

    @Deprecated
    private HTTPResponse getCSS(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, "text/css");
    }

    @Deprecated
    private HTTPResponse getJS(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, "text/javascript");
    }

    @Deprecated
    private HTTPResponse getPNG(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, "image/png");
    }

    @Deprecated
    private HTTPResponse getTXT(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, "text/plain");
    }
    
    private HTTPResponse getJSON(HTTPRequest request, GCPResponse object) {
        HTTPCode code;
        String message;
        String protocol;
        byte[] content;

        if (object != null) {
            Gson gson = new Gson();
            String toJson = gson.toJson(object);
            content = toJson.getBytes();
        } else {
            content = "".getBytes();
        }
        code = HTTPCode.OK;
        message = "OK";
        protocol = request.getProtocol();

        String dateGMT = getDateGTM();

        HTTPHeader header = new HTTPHeader();
        header.addAttribute("Location", "http://localhost:8080/");
        header.addAttribute("Date", dateGMT);
        header.addAttribute("Server", "RMelocaServer/1.0");
        header.addAttribute("Content-Type", "application/json");
        header.addAttribute("Content-Length", String.valueOf(content.length));

        HTTPResponse response = new HTTPResponse(protocol, code, message, content, header);
        return response;
    }

    private HTTPResponse getJSON(HTTPRequest request, String path) {
        HTTPCode code;
        String message;
        String protocol;
        byte[] content;

        try {
            content = buildJSONFolder(path).toString().getBytes();
        } catch (FileNotFoundException ex) {
            content = "".getBytes();
        }
        code = HTTPCode.OK;
        message = "OK";
        protocol = request.getProtocol();

        String dateGMT = getDateGTM();

        HTTPHeader header = new HTTPHeader();
        header.addAttribute("Location", "http://localhost:8080/");
        header.addAttribute("Date", dateGMT);
        header.addAttribute("Server", "RMelocaServer/1.0");
        header.addAttribute("Content-Type", "application/json");
        header.addAttribute("Content-Length", String.valueOf(content.length));

        HTTPResponse response = new HTTPResponse(protocol, code, message, content, header);
        return response;
    }

    private JsonObject buildJSONFolder(String path) throws FileNotFoundException {
        JsonObject jsonFolder = new JsonObject();
        File folder = new File(Server.RESOURCES_PATH, path);
        if (!folder.exists()) {
            throw new FileNotFoundException();
        }
        File[] files = folder.listFiles();
        for (File file : files) {
            String lastModified = parseDate(new Date(file.lastModified()));
            long totalSpace = file.length();
            boolean isNavigable = isNavigable(file);
            JsonObject jsonFile = new JsonObject();
            jsonFile.addProperty("size", totalSpace);
            jsonFile.addProperty("lastModified", lastModified);
            jsonFile.addProperty("isNavigable", isNavigable);
            jsonFolder.add(file.getName(), jsonFile);
        }
        return jsonFolder;
    }

    private boolean isNavigable(File file) {
        if (file.isDirectory()) {
            return true;
        } else if (file.getName().endsWith(".js")) {
            return true;
        } else if (file.getName().endsWith(".html")) {
            return true;
        } else if (file.getName().endsWith(".css")) {
            return true;
        } else if (file.getName().endsWith(".txt")) {
            return true;
        }
        return false;
    }

    private Object getGameResource(String path) {
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
        if (path.startsWith("/game/profiles")) {
            String[] url = path.split("/");
            if (url.length < 4) {
                return game.getProfiles();
            }
            String profileName = url[3];
            try {
                return game.getProfile(new Profile(profileName));
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else if (path.startsWith("/game")) {
            return game;
        }
        return null;
    }

    private void postGameResource(HTTPRequest request, String path) {
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
        if (path.startsWith("/game/profiles")) {
            Gson gson = new Gson();
            Profile profile = gson.fromJson(request.getContent(), Profile.class);
            game.addProfile(profile);
            try {
                gameController.update(game);
            } catch (ItemNotFoundException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean resourceExists(String path) {
        File file = new File(Server.RESOURCES_PATH, path);
        return file.exists();
    }
}
