package org.apache.tika.parser;

import org.apache.tika.parser.ParseContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

public class ParseContext_toString_16_0_Test {

    @Test
    public void testToString() {
        ParseContext parseContext = new ParseContext();
        String expectedToString = "ParseContext{" + "context=" + Collections.emptyMap() + ", jsonConfigs=" + Collections.emptyMap() + '}';
        assertEquals(expectedToString, parseContext.toString());
    }
}
