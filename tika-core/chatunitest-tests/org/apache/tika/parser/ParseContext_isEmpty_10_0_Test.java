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

@ExtendWith(MockitoExtension.class)
public class ParseContext_isEmpty_10_0_Test {

    @Mock
    private Metadata metadata;

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        parseContext = new ParseContext();
    }

    @Test
    public void testIsEmpty_WhenContextAndJsonConfigsAreEmpty() {
        assertTrue(parseContext.isEmpty());
    }

    @Test
    public void testIsEmpty_WhenContextIsNotEmpty() {
        parseContext.set(String.class, "testValue");
        assertFalse(parseContext.isEmpty());
    }

    @Test
    public void testIsEmpty_WhenJsonConfigsIsNotEmpty() {
        JsonConfig mockJsonConfig = mock(JsonConfig.class);
        parseContext.setJsonConfig("testKey", mockJsonConfig);
        assertFalse(parseContext.isEmpty());
    }

    @Test
    public void testIsEmpty_WhenBothContextAndJsonConfigsAreNotEmpty() {
        parseContext.set(String.class, "testValue");
        JsonConfig mockJsonConfig = mock(JsonConfig.class);
        parseContext.setJsonConfig("testKey", mockJsonConfig);
        assertFalse(parseContext.isEmpty());
    }
}
