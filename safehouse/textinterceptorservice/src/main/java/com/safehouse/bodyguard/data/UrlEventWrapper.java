package com.safehouse.bodyguard.data;

public class UrlEventWrapper {
    private final String url;
    private final String idOfEvent;

    public UrlEventWrapper(String url, String idOfEvent) {

        this.url = url;
        this.idOfEvent = idOfEvent;
    }

    public String getUrl() {
        return url;
    }

    public String getIdOfEvent() {
        return idOfEvent;
    }
}
