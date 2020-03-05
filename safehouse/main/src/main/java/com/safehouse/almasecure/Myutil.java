package com.safehouse.almasecure;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import de.blinkt.openvpn.R;

public class Myutil {

    HttpURLConnection urlConnection;

    public int getStatus(String url2, String apivalue) {


        try {

            URL url = new URL(url2 + apivalue);
             urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            int status = urlConnection.getResponseCode();
            return status;
          }


        catch (ProtocolException ex) {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return 0;
    }
    public InputStream getInStream() {

        try {

            return urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public InputStream getError() {

        return urlConnection.getErrorStream();
    }
    public OutputStream getOutStream() {

        try {

            return urlConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setvalue(HashMap<String,String> myvalue) {


        OutputStream os = null;
        try {
            os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(Util.getPostDataString(myvalue));
            writer.flush();
            writer.close();
            os.close();
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    }

