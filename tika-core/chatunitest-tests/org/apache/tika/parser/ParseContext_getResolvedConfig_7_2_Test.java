package org.apache.tika.parser;

import java.util.HashMap;
import java.util.Map;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;
import java.lang.reflect.Field;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParseContext_getResolvedConfig_7_2_Test {

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
    }

    @Test
    public void testGetResolvedConfig_whenResolvedConfigsIsNull() throws Exception {
        // Arrange
        Field resolvedConfigsField = parseContext.getClass().getDeclaredField("resolvedConfigs");
        resolvedConfigsField.setAccessible(true);
        resolvedConfigsField.set(parseContext, null);
        // Act
        Object result = parseContext.getResolvedConfig("testKey");
        // Assert
        assertNull(result);
    }

    @Test
    public void testGetResolvedConfig_whenResolvedConfigsIsNotEmpty() throws Exception {
        // Arrange
        Field resolvedConfigsField = parseContext.getClass().getDeclaredField("resolvedConfigs");
        resolvedConfigsField.setAccessible(true);
        Map<String, Object> resolvedConfigs = new HashMap<>();
        resolvedConfigs.put("testKey", "testValue");
        resolvedConfigsField.set(parseContext, resolvedConfigs);
        // Act
        Object result = parseContext.getResolvedConfig("testKey");
        // Assert
        assertEquals("testValue", result);
    }

    @Test
    public void testGetResolvedConfig_whenResolvedConfigsContainsNull() throws Exception {
        // Arrange
        Field resolvedConfigsField = parseContext.getClass().getDeclaredField("resolvedConfigs");
        resolvedConfigsField.setAccessible(true);
        Map<String, Object> resolvedConfigs = new HashMap<>();
        resolvedConfigs.put("testKey", null);
        resolvedConfigsField.set(parseContext, resolvedConfigs);
        // Act
        Object result = parseContext.getResolvedConfig("testKey");
        // Assert
        assertNull(result);
    }
}
