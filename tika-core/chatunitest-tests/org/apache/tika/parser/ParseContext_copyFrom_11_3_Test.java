package org.apache.tika.parser;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

@ExtendWith(MockitoExtension.class)
public class ParseContext_copyFrom_11_3_Test {

    @Mock
    private JsonConfig mockJsonConfig;

    private ParseContext sourceContext;

    private ParseContext targetContext;

    @BeforeEach
    public void setUp() {
        sourceContext = new ParseContext();
        targetContext = new ParseContext();
        // Initialize source context with some data
        sourceContext.set(String.class, "testValue");
        sourceContext.setJsonConfig("testKey", mockJsonConfig);
        sourceContext.setResolvedConfig("testKey", new Object());
        // Initialize target context with some data
        targetContext.set(String.class, "initialValue");
        targetContext.setJsonConfig("initialKey", mockJsonConfig);
        targetContext.setResolvedConfig("initialKey", new Object());
    }

    @Test
    public void testCopyFrom() throws Exception {
        // Call the copyFrom method
        targetContext.copyFrom(sourceContext);
        // Verify that the source context data has been copied to the target context
        assertEquals("testValue", targetContext.getContextMap().get(String.class));
        assertEquals(mockJsonConfig, targetContext.getJsonConfigs().get("testKey"));
        assertEquals(new Object(), targetContext.getResolvedConfig("testKey"));
        // Verify that the target context data has been cleared
        assertNull(targetContext.getContextMap().get("initialValue"));
        assertNull(targetContext.getJsonConfigs().get("initialKey"));
        assertNull(targetContext.getResolvedConfig("initialKey"));
    }

    @Test
    public void testCopyFromWithNullSource() throws Exception {
        // Call the copyFrom method with a null source
        targetContext.copyFrom(null);
        // Verify that the target context data remains unchanged
        assertEquals("initialValue", targetContext.getContextMap().get("initialValue"));
        assertEquals(mockJsonConfig, targetContext.getJsonConfigs().get("initialKey"));
        assertEquals(new Object(), targetContext.getResolvedConfig("initialKey"));
    }
}
