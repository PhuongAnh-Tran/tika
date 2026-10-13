package org.apache.tika.parser;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

@ExtendWith(MockitoExtension.class)
public class ParseContext_get_1_1_Test {

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
    }

    @Test
    public void testGetWithNonExistentKey() {
        assertNull(parseContext.get(String.class));
    }

    @Test
    public void testGetWithExistingKey() {
        parseContext.set(String.class, "testValue");
        assertEquals("testValue", parseContext.get(String.class));
    }
}
