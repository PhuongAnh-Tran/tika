package org.apache.tika.parser;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

@ExtendWith(MockitoExtension.class)
public class ParseContext_getJsonConfig_5_0_Test {

    @InjectMocks
    private ParseContext parseContext;

    @Mock
    private JsonConfig jsonConfig;

    @BeforeEach
    public void setUp() {
        parseContext.setJsonConfig("testConfig", jsonConfig);
    }

    @Test
    public void testGetJsonConfig() {
        JsonConfig result = parseContext.getJsonConfig("testConfig");
        assertEquals(jsonConfig, result);
    }
}
