package com.safehouse.bodyguard.data;

import java.util.Objects;

public class AccesibilityEventAggregator {

    private StringBuffer buffer = new StringBuffer();
    private StringBuffer classSetBuffer= new StringBuffer();

    public void appendString(CharSequence text) {
        buffer.append(text).append(" ");
    }

    public void appendClassHirachy(CharSequence className) {
        classSetBuffer.append(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccesibilityEventAggregator that = (AccesibilityEventAggregator) o;
        return Objects.equals(buffer, that.buffer) &&
                Objects.equals(classSetBuffer, that.classSetBuffer);
    }


    public String getIdentifierKey() {
        return String.valueOf(buffer.toString().hashCode());
    }

    public String getText() {
        return buffer.toString();
    }
}
