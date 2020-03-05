package com.safehouse.bodyguard.output;

/**
 * any output we want such as log or broadcast
 */
public interface ActionOnUrlFound {
    void execute(String urls);
}
