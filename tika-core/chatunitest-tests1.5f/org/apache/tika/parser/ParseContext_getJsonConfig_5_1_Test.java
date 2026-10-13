package org.apache.tika.parser;

import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
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

@ExtendWith(MockitoExtension.class)
public class ParseContext_getJsonConfig_5_1_Test {

    @Mock
    private JsonConfig config1;

    @Mock
    private JsonConfig config2;

    private ParseContext context;

    @BeforeEach
    public void setup() {
        context = new ParseContext();
        context.setJsonConfig("config1", config1);
        context.setJsonConfig("config2", config2);
    }

    @Test
    public void testGetJsonConfig() {
        assertEquals(config1, context.getJsonConfig("config1"));
        assertEquals(config2, context.getJsonConfig("config2"));
        assertNull(context.getJsonConfig("nonexistent"));
    }

    @Test
    public void testGetJsonConfigs() {
        Map<String, JsonConfig> configs = context.getJsonConfigs();
        assertEquals(2, configs.size());
        assertEquals(config1, configs.get("config1"));
        assertEquals(config2, configs.get("config2"));
    }

    @Test
    public void testGetJsonConfigWithNullName() {
        assertThrows(NullPointerException.class, () -> context.getJsonConfig(null));
    }

    @Test
    public void testGetJsonConfigWithEmptyName() {
        assertNull(context.getJsonConfig(""));
    }
}
