package org.apache.tika.parser;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

public class ParseContext_get_2_1_Test {

    private ParseContext context;

    @BeforeEach
    public void setUp() {
        context = new ParseContext();
    }

    @Test
    public void testGetWithValue() {
        context.set(String.class, "test");
        assertEquals("test", context.get(String.class, null));
    }

    @Test
    public void testGetWithDefaultValue() {
        assertEquals("default", context.get(String.class, "default"));
    }

    @Test
    public void testGetWithNullValue() {
        assertNull(context.get(String.class, null));
    }

    @Test
    public void testGetWithNonExistentKey() {
        assertNull(context.get(Integer.class, null));
    }
}
