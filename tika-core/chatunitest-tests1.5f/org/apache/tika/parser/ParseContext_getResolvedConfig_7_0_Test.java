package org.apache.tika.parser;

import org.apache.tika.parser.ParseContext;
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

public class ParseContext_getResolvedConfig_7_0_Test {

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
    }

    @Test
    public void testGetResolvedConfig_nullName_nullResult() {
        assertNull(parseContext.getResolvedConfig(null));
    }

    @Test
    public void testGetResolvedConfig_emptyName_nullResult() {
        assertNull(parseContext.getResolvedConfig(""));
    }

    @Test
    public void testGetResolvedConfig_unknownName_nullResult() {
        assertNull(parseContext.getResolvedConfig("unknown"));
    }

    @Test
    public void testGetResolvedConfig_knownName_nonNullResult() {
        String name = "known";
        Object value = new Object();
        parseContext.setResolvedConfig(name, value);
        assertEquals(value, parseContext.getResolvedConfig(name));
    }

    @Test
    public void testGetResolvedConfig_knownName_nullResultAfterClear() {
        String name = "known";
        Object value = new Object();
        parseContext.setResolvedConfig(name, value);
        parseContext.setResolvedConfig(name, null);
        assertNull(parseContext.getResolvedConfig(name));
    }

    @Test
    public void testGetResolvedConfig_knownName_nonNullResultAfterSet() {
        String name = "known";
        Object value = new Object();
        parseContext.setResolvedConfig(name, value);
        Object newValue = new Object();
        parseContext.setResolvedConfig(name, newValue);
        assertEquals(newValue, parseContext.getResolvedConfig(name));
    }
}
