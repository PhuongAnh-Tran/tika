package org.apache.tika.parser;

import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiter;
import org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory;
import org.apache.tika.parser.ParseContext;
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
public class ParseContext_newMetadata_12_4_Test {

    @Mock
    private MetadataWriteLimiterFactory metadataWriteLimiterFactory;

    @Mock
    private MetadataWriteLimiter metadataWriteLimiter;

    @BeforeEach
    public void setUp() {
        when(metadataWriteLimiterFactory.newInstance()).thenReturn(metadataWriteLimiter);
    }

    @Test
    public void testNewMetadata() throws Exception {
        // Arrange
        ParseContext parseContext = new ParseContext();
        parseContext.set(MetadataWriteLimiterFactory.class, metadataWriteLimiterFactory);
        // Act
        Metadata result = parseContext.newMetadata();
        // Assert
        assertEquals(Metadata.class, result.getClass());
        verify(metadataWriteLimiterFactory, times(1)).newInstance();
        verify(metadataWriteLimiter, never()).set(anyString(), anyString(), anyMap());
    }
}
