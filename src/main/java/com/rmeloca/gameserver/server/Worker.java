/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
            HTTPResponse httpResponse;
            switch (httpRequest.getMethod()) {
                case GET:
                    path = httpRequest.getResource();
                    if (path.startsWith("/game")) {
                        GameHandler gameHandler = new GameHandler();
                        GCPResponse gcpResponse = gameHandler.getGameResource(path);
                        httpResponse = getJSON(httpRequest, gcpResponse);
//                    } else if (path.equals("/inVhand")) {
//                        path = "/inVhand/index.html";
//                        httpResponse = getHTML(httpRequest, path);
                    } else if (path.startsWith("/files/")) {
                        path = path.replaceFirst("/files/", "/");
                        httpResponse = getJSON(httpRequest, path);
                    } else if (resourceExists(path)) {
                        if (path.contains(".")) {
                            httpResponse = getFile(httpRequest, path);
                        } else {
                            path = "index.html";
                            httpResponse = getHTML(httpRequest, path);
                        }
                    } else {
                        path = "-";
                        httpResponse = getHTML(httpRequest, path);
                    }
                    break;

                case POST:
                    path = httpRequest.getResource();
                    if (path.startsWith("/game")) {
                        GameHandler gameHandler = new GameHandler();
                        GCPResponse postGameResource = gameHandler.postGameResource(httpRequest, path);
                        httpResponse = getJSON(httpRequest, postGameResource);
                    } else {
                        path = "-";
                        httpResponse = getHTML(httpRequest, path);
                    }
                    break;
                default:
                    throw new AssertionError(httpRequest.getMethod().name());
            }
            httpResponse.send(this.outputStream);
            Logger.getLogger(Worker.class.getName()).log(Level.INFO, "{0} {1}", new Object[]{String.valueOf(httpResponse.getCode()), path});
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

    private HTTPResponse getHTML(HTTPRequest httpRequest, String path) throws IOException {
        return getFile(httpRequest, path, "text/html");
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

    private HTTPResponse getFile(HTTPRequest request, String path) throws IOException {
        return getFile(request, path, detectMimeType(path));
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

    private String detectMimeType(String url) {
        if (url.endsWith(".html")) {
            return "text/html";
        } else if (url.endsWith(".css")) {
            return "text/css";
        } else if (url.endsWith(".json")) {
            return "application/json";
        } else if (url.endsWith(".js")) {
            return "text/javascript";
        } else if (url.endsWith(".png")) {
            return "image/png";
        } else if (url.endsWith(".txt")) {
            return "text/plain";
        }
        return "application/octet-stream";
    }

    private HTTPResponse getJSON(HTTPRequest request, Object object) {
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

    private boolean resourceExists(String path) {
        File file = new File(Server.RESOURCES_PATH, path);
        return file.exists();
    }
}
