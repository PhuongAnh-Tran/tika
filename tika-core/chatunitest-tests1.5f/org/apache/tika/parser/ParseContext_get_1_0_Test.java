package org.apache.tika.parser;

import org.apache.tika.parser.ParseContext;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

public class ParseContext_get_1_0_Test {

    @Test
    public void testGet() throws Exception {
        ParseContext context = new ParseContext();
        context.set(Metadata.class, new Metadata());
        Metadata metadata = context.get(Metadata.class);
        assertEquals(Metadata.class, metadata.getClass());
    }
}
