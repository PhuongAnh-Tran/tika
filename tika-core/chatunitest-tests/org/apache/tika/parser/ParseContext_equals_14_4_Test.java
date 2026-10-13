package org.apache.tika.parser;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParseContext_equals_14_4_Test {

    private ParseContext parseContext;

    private ParseContext otherParseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
        otherParseContext = new ParseContext();
        // Set up initial state for parseContext
        parseContext.set(String.class, "testValue");
        JsonConfig mockJsonConfig = mock(JsonConfig.class);
        parseContext.setJsonConfig("testJsonConfig", mockJsonConfig);
        // Set up initial state for otherParseContext
        otherParseContext.set(String.class, "testValue");
        JsonConfig mockJsonConfigOther = mock(JsonConfig.class);
        otherParseContext.setJsonConfig("testJsonConfig", mockJsonConfigOther);
    }

    @Test
    public void testEqualsReflexive() {
        assertTrue(parseContext.equals(parseContext));
    }

    @Test
    public void testEqualsSymmetric() {
        assertTrue(parseContext.equals(otherParseContext) && otherParseContext.equals(parseContext));
    }

    @Test
    public void testEqualsTransitive() {
        ParseContext thirdParseContext = new ParseContext();
        thirdParseContext.set(String.class, "testValue");
        JsonConfig mockJsonConfigThird = mock(JsonConfig.class);
        thirdParseContext.setJsonConfig("testJsonConfig", mockJsonConfigThird);
        assertTrue(parseContext.equals(otherParseContext) && otherParseContext.equals(thirdParseContext) && parseContext.equals(thirdParseContext));
    }

    @Test
    public void testEqualsNull() {
        assertFalse(parseContext.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        assertFalse(parseContext.equals("testString"));
    }

    @Test
    public void testEqualsDifferentContext() {
        otherParseContext.set(String.class, "differentValue");
        assertFalse(parseContext.equals(otherParseContext));
    }

    @Test
    public void testEqualsDifferentJsonConfig() {
        otherParseContext.setJsonConfig("testJsonConfig", mock(JsonConfig.class));
        assertFalse(parseContext.equals(otherParseContext));
    }
}
