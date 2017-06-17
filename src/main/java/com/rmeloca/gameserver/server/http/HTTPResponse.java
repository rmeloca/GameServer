/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmeloca.gameserver.server.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author romulo
 */
public class HTTPResponse {

    private final String protocol;
    private final HTTPCode code;
    private final String message;
    private final byte[] content;
    private final HTTPHeader header;

    public HTTPResponse(String protocol, HTTPCode code, String message, byte[] content, HTTPHeader header) {
        this.protocol = protocol;
        this.code = code;
        this.message = message;
        this.content = content;
        this.header = header;
    }

    public void send(OutputStream outputStream) throws IOException {
        outputStream.write(this.toString().getBytes());
        outputStream.write(this.content);
        outputStream.flush();
    }

    public HTTPCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.protocol).append(" ").append(this.code.getValue()).append(" ").append(this.code).append("\r\n");
        for (Map.Entry<String, List<String>> entry : this.header) {
            stringBuilder.append(entry.getKey());
            String valueArray = Arrays.toString(entry.getValue().toArray()).replace("[", "").replace("]", "");
            stringBuilder.append(": ").append(valueArray).append("\r\n");
        }
        stringBuilder.append("\r\n");
        stringBuilder.append(this.message);
        return stringBuilder.toString();
    }
}
