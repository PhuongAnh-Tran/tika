package org.apache.tika.parser;

import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;

public class ParseContext_isEmpty_10_1_Test {

    @Test
    void testSet() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        // set a value to context
        context.set(String.class, "test");
        assertFalse(context.isEmpty());
        // set a value to jsonConfigs
        JsonConfig config = mock(JsonConfig.class);
        context.setJsonConfig("name", config);
        assertFalse(context.isEmpty());
    }

    @Test
    void testSetJsonConfig() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        // set jsonConfigs with json
        context.setJsonConfig("name", "json");
        assertFalse(context.isEmpty());
        // set jsonConfigs with JsonConfig
        JsonConfig config = mock(JsonConfig.class);
        context.setJsonConfig("name", config);
        assertFalse(context.isEmpty());
    }

    @Test
    void testGetJsonConfigs() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        // get jsonConfigs
        Map<String, JsonConfig> jsonConfigs = context.getJsonConfigs();
        assertTrue(jsonConfigs.isEmpty());
        // add an entry to jsonConfigs
        JsonConfig config = mock(JsonConfig.class);
        context.setJsonConfig("name", config);
        jsonConfigs = context.getJsonConfigs();
        assertFalse(jsonConfigs.isEmpty());
    }

    @Test
    void testGetContextMap() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        // get contextMap
        Map<String, Object> contextMap = context.getContextMap();
        assertTrue(contextMap.isEmpty());
        // add an entry to context
        context.set(String.class, "test");
        contextMap = context.getContextMap();
        assertFalse(contextMap.isEmpty());
    }
}
