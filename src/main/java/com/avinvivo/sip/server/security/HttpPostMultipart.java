/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.security;

/**
 *
 * @author Kilian
 */
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import static java.util.Base64.getEncoder;
import java.util.Map;


public class HttpPostMultipart {
    private final HttpURLConnection httpConn;
    private final String charset;
    private final OutputStream outputStream;
    private final PrintWriter writer;
    private final String clientName;
    private final String password;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @param headers
     * @throws IOException
     */
    public HttpPostMultipart(URL requestURL, String charset, String user, String password) throws IOException {
        this.charset = charset;
        this.clientName = user;
        this.password = password;
        httpConn = (HttpURLConnection) requestURL.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Authorization", createBasicAuthHeaderValue());
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    private String createBasicAuthHeaderValue() {
        String auth = this.clientName + ":" + this.password;
        String encodedString = getEncoder().encodeToString(auth.getBytes());
        return "Basic " + encodedString;
    }

    public StringBuilder addData(Map<String, String> parameters) throws UnsupportedEncodingException {

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {

            if (isInvalidPostVariable(entry)) {
                continue;
            }
            data.append("&");
            data.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            data.append("=");
            data.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        writer.write(data.toString());
        return data;

    }

    private boolean isInvalidPostVariable(Map.Entry<String, String> entry) {
            return entry.getKey() == null || entry.getValue() == null;
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return String as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response = "";
        writer.flush();
        writer.close();

        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = httpConn.getInputStream().read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            response = result.toString(this.charset);
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }
}