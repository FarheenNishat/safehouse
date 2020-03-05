package com.safehouse.bodyguard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;

public class JsonParseUtils<P>
{
    public P readJsonFromFile(InputStream is, Type className, GsonBuilder gsonBuilder)
    {
        GsonBuilder builder;
        if(gsonBuilder!=null)
        {
            builder=gsonBuilder;
        }else
        {
            builder=new GsonBuilder();
        }

        Gson gson = builder.excludeFieldsWithoutExposeAnnotation().create();

        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            return gson.fromJson(reader, className);

        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        return null;
    }

    public boolean writeToJsonFile(File fileToSaveTo, Type className, GsonBuilder gsonBuilder, P objectsToWrite)
    {
        GsonBuilder builder;
        if(gsonBuilder!=null)
        {
            builder=gsonBuilder;
        }else
        {
            builder=new GsonBuilder();
        }

        Gson gson = builder.create();
        try(FileWriter writer=new FileWriter(fileToSaveTo))
        {
            gson.toJson(objectsToWrite,className,gson.newJsonWriter(writer));
            return true;
        } catch (IOException e) {
            return false;
        }

    }
}

