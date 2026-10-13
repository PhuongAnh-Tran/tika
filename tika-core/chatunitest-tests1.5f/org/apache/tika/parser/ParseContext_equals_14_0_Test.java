package org.apache.tika.parser;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParseContext_equals_14_0_Test {

    @Mock
    private JsonConfig jsonConfig1;

    @Mock
    private JsonConfig jsonConfig2;

    @Mock
    private JsonConfig jsonConfig3;

    @Mock
    private JsonConfig jsonConfig4;

    @Mock
    private JsonConfig jsonConfig5;

    @Mock
    private JsonConfig jsonConfig6;

    @Test
    void testEquals() throws Exception {
        // Create two ParseContext objects with the same context and jsonConfigs
        ParseContext context1 = new ParseContext();
        Metadata metadata1 = new Metadata();
        context1.set(Metadata.class, metadata1);
        context1.setJsonConfig("name", jsonConfig1);
        ParseContext context2 = new ParseContext();
        Metadata metadata2 = new Metadata();
        context2.set(Metadata.class, metadata2);
        context2.setJsonConfig("name", jsonConfig2);
        // Test that they are equal
        assertTrue(context1.equals(context2));
        // Create a ParseContext object with different context and jsonConfigs
        ParseContext context3 = new ParseContext();
        Metadata metadata3 = new Metadata();
        context3.set(Metadata.class, metadata3);
        context3.setJsonConfig("name", jsonConfig3);
        // Test that they are not equal
        assertFalse(context1.equals(context3));
        // Create a ParseContext object with the same context but different jsonConfigs
        ParseContext context4 = new ParseContext();
        Metadata metadata4 = new Metadata();
        context4.set(Metadata.class, metadata4);
        context4.setJsonConfig("name", jsonConfig4);
        // Test that they are not equal
        assertFalse(context1.equals(context4));
        // Create a ParseContext object with different context but the same jsonConfigs
        ParseContext context5 = new ParseContext();
        Metadata metadata5 = new Metadata();
        context5.set(Metadata.class, metadata5);
        context5.setJsonConfig("name", jsonConfig5);
        // Test that they are not equal
        assertFalse(context1.equals(context5));
        // Create a ParseContext object with the same context and jsonConfigs but different resolvedConfigs
        ParseContext context6 = new ParseContext();
        Metadata metadata6 = new Metadata();
        context6.set(Metadata.class, metadata6);
        context6.setJsonConfig("name", jsonConfig6);
        context6.setResolvedConfig("name", new Object());
        // Test that they are not equal
        assertFalse(context1.equals(context6));
        // Test that null is not equal to a ParseContext object
        assertFalse(context1.equals(null));
    }
}
