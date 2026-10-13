package org.apache.tika.parser;

import java.util.HashMap;
import java.util.Map;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.apache.tika.parser.ParseContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Serializable;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ParseContext_getJsonConfig_5_2_Test {

    @Mock
    private JsonConfig jsonConfig;

    @Test
    public void testGetJsonConfig() throws Exception {
        ParseContext context = new ParseContext();
        context.setJsonConfig("name", jsonConfig);
        assertEquals(jsonConfig, context.getJsonConfig("name"));
        assertNull(context.getJsonConfig("non-existent"));
        assertThrows(IllegalArgumentException.class, () -> context.getJsonConfig(null));
    }
}
