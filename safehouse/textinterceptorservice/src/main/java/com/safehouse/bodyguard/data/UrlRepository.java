package com.safehouse.bodyguard.data;

import java.util.HashMap;

/**
 * Holds the data, currently we save the url's in memory to avoid duplicates but here we will be able to connect to a DB in the future for real time processing or something of the sort
 */
public class UrlRepository {
    private HashMap<String, String> mEventsMap = new HashMap<>();
    private HashMap<String, AccesibilityEventAggregator> mAccesibilityEventsMap = new HashMap<String, AccesibilityEventAggregator>();

    public UrlRepository() {

    }

    public boolean isRepeatedEvent(UrlEventWrapper urlEventWrapper) {

        return mEventsMap.containsKey(urlEventWrapper.getUrl());

    }

    public boolean saveUrlEvent(UrlEventWrapper urlEventWrapper) {

        if(mEventsMap.containsKey(urlEventWrapper.getIdOfEvent()))
            return false;

        mEventsMap.put(urlEventWrapper.getUrl(),urlEventWrapper.getUrl());

        return true;

    }


    public void saveAccesibilityEvent(AccesibilityEventAggregator accesibilityEventAggregator) {
        mAccesibilityEventsMap.put(accesibilityEventAggregator.getIdentifierKey(), accesibilityEventAggregator);
    }


    public boolean isEventBeenProcessed(AccesibilityEventAggregator idOfEvent) {
        return mAccesibilityEventsMap.containsKey(idOfEvent.getIdentifierKey());
    }

    public void reset() {
        mEventsMap.clear();
        mAccesibilityEventsMap.clear();
    }
}