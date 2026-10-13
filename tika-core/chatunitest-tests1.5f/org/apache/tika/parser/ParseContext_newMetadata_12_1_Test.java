package org.apache.tika.parser;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import java.lang.reflect.Method;
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
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;

class ParseContext_newMetadata_12_1_Test {

    @Test
    void testNewMetadata() throws Exception {
        ParseContext context = new ParseContext();
        Method method = ParseContext.class.getDeclaredMethod("newMetadata");
        method.setAccessible(true);
        Metadata metadata = (Metadata) method.invoke(context);
        assertNotNull(metadata);
        assertEquals(0, metadata.size());
    }
}
