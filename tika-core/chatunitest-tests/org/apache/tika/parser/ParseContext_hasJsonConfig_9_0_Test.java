package org.apache.tika.parser;

import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ParseContext_hasJsonConfig_9_0_Test {

    @Mock
    private JsonConfig jsonConfig;

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
    }

    @Test
    public void testHasJsonConfigTrue() {
        parseContext.setJsonConfig("testName", jsonConfig);
        assertTrue(parseContext.hasJsonConfig("testName"));
    }

    @Test
    public void testHasJsonConfigFalse() {
        assertFalse(parseContext.hasJsonConfig("testName"));
    }
}
