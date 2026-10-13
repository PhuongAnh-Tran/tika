package org.apache.tika.parser;

import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParseContext_get_2_1_Test {

    @Mock
    private Metadata metadata;

    @Test
    public void testGetWithExistingKey() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("testValue", result);
    }

    @Test
    public void testGetWithNonExistingKey() {
        ParseContext parseContext = new ParseContext();
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("defaultValue", result);
    }

    @Test
    public void testGetWithDefaultValue() {
        ParseContext parseContext = new ParseContext();
        String result = parseContext.get(String.class, null);
        assertEquals(null, result);
    }

    @Test
    public void testGetWithNonGenericKey() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("testValue", result);
    }

    @Test
    public void testGetWithNullDefaultValue() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, null);
        assertEquals("testValue", result);
    }

    @Test
    public void testGetWithEmptyKey() {
        ParseContext parseContext = new ParseContext();
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("defaultValue", result);
    }

    @Test
    public void testGetWithEmptyDefaultValue() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, "");
        assertEquals("testValue", result);
    }
}
