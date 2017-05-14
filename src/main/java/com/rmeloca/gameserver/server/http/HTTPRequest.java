/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 *
 * @author romulo
 */
public class HTTPRequest {

    private final HTTPMethod method;
    private final String protocol;
    private final String resource;
    private final boolean keepAlive;
    private final int timeOut;
    private final HTTPHeader header;
    private final String content;
    private final HTTPParameters parameters;

    public HTTPRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String dataRequestLine = bufferedReader.readLine();
        if (dataRequestLine == null) {
            throw new RuntimeException("Empty line reading");
        }
        String[] dataRequest = dataRequestLine.split(" ");
        this.method = HTTPMethod.valueOf(dataRequest[0]);

        if (dataRequest[1].contains("?")) {
            String[] urlSplit = dataRequest[1].split("?");
            this.resource = urlSplit[0];
            this.parameters = new HTTPParameters(urlSplit[1]);
        } else {
            this.resource = dataRequest[1];
            this.parameters = new HTTPParameters();
        }

        this.protocol = dataRequest[2];

        this.header = new HTTPHeader();
        String dataHeaderLine = bufferedReader.readLine();
        while (dataHeaderLine != null && !dataHeaderLine.isEmpty()) {
            String[] dataHeader = dataHeaderLine.split(":");
            this.header.addAttribute(dataHeader[0], dataHeader[1].trim().split(","));
            dataHeaderLine = bufferedReader.readLine();
        }

        if (this.header.containsProperty("Connection")) {
            this.keepAlive = this.header.getProperty("Connection").get(0).equals("keep-alive");
        } else {
            this.keepAlive = false;
        }

        if (this.header.containsProperty("Content-Length")) {
            List<String> lengthStringList = this.header.getProperty("Content-Length");
            int length = Integer.valueOf(lengthStringList.get(0));
            this.content = readMessageBody(bufferedReader, length);
        } else {
            this.content = null;
        }

        this.timeOut = 3000;
    }

    private String readMessageBody(Reader inputStream, int size) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int read = inputStream.read();
            stringBuilder.append((char) read);
        }
        return stringBuilder.toString();
    }

    public String getResource() {
        return resource;
    }

    public String getProtocol() {
        return protocol;
    }

    public HTTPMethod getMethod() {
        return this.method;
    }

    public String getContent() {
        return content;
    }

}
