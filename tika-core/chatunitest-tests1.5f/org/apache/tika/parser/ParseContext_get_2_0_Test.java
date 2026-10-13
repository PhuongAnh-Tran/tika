package org.apache.tika.parser;

import org.apache.tika.parser.ParseContext;
import java.lang.reflect.Field;
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
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

public class ParseContext_get_2_0_Test {

    @Test
    public void getTest() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("key", "value");
        Field field = ParseContext.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(context, contextMap);
        String key = "key";
        String defaultValue = "defaultValue";
        String expectedValue = "value";
        String result = context.get(String.class, defaultValue);
        assert result.equals(expectedValue);
    }

    @Test
    public void getTestWithNull() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("key", "value");
        Field field = ParseContext.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(context, contextMap);
        String key = "key";
        String defaultValue = null;
        String expectedValue = "value";
        String result = context.get(String.class, defaultValue);
        assert result.equals(expectedValue);
    }

    @Test
    public void getTestWithNonExistentKey() throws NoSuchFieldException, IllegalAccessException {
        ParseContext context = new ParseContext();
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("key", "value");
        Field field = ParseContext.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(context, contextMap);
        String key = "nonExistentKey";
        String defaultValue = "defaultValue";
        String result = context.get(String.class, defaultValue);
        assert result.equals(defaultValue);
    }
}
