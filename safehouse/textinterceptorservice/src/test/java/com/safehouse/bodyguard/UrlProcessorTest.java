package com.safehouse.bodyguard;

import com.safehouse.bodyguard.urlProcessing.UrlProcessor;
import com.safehouse.bodyguard.urlProcessing.UrlSearchStrategyRegexBased;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UrlProcessorTest {
    private UrlProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new UrlProcessor(new UrlSearchStrategyRegexBased());
    }

    @Test
    public void findUrlsInText() {


        String[] urlsAsStrings=new String[]{"www.url.com"};

        List<String> urls= processor.findUrlsInText("bla bla bla www.url.com ddd");
        assertArrayEquals(urls.toArray(),urlsAsStrings);


        urls= processor.findUrlsInText("bla bla bla www.url.com ddd");
        assertArrayEquals(urls.toArray(),urlsAsStrings);

        urls= processor.findUrlsInText("http");
        assertEquals(0, urls.size());

        urls= processor.findUrlsInText("gmail.com");
        assertEquals(1, urls.size());
    }

    @Test
    public void testNull() {

        List<String> urls= processor.findUrlsInText(null);
        assertEquals(0,urls.size());
    }

    @Test
    public void findEmptyInText() {

        List<String> urls= processor.findUrlsInText("");
        assertEquals(0,urls.size());


        urls= processor.findUrlsInText("sdfsdfsdfsdfsdf     ");
        assertEquals(0,urls.size());

        urls= processor.findUrlsInText("gooo ggg gggg moshe wwww list. ddd     ");
        assertEquals(0,urls.size());
    }


}