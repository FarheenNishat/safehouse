package com.safehouse.bodyguard.urlProcessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for the url's in the text
 *
 */
public class UrlProcessor {

    UrlSearchStrategyRegexBased mUrlSearchStrategy;

    public UrlProcessor(UrlSearchStrategyRegexBased mUrlSearchStrategy) {
        this.mUrlSearchStrategy = mUrlSearchStrategy;
    }

    public List<String> findUrlsInText(String splitAllTextToSections) {


        if(splitAllTextToSections==null)
            return new ArrayList<>();

        return mUrlSearchStrategy.searchUrl(splitAllTextToSections);


    }
}
