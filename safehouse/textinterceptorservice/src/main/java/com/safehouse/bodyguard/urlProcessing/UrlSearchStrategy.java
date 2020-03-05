package com.safehouse.bodyguard.urlProcessing;

import java.util.List;

interface UrlSearchStrategy {
    List<String> searchUrl(String splitAllTextToSections);
}
