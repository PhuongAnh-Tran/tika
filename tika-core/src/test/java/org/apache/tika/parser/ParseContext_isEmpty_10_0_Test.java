/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.tika.config.JsonConfig;
import org.apache.tika.metadata.Metadata;

@ExtendWith(MockitoExtension.class)
public class ParseContext_isEmpty_10_0_Test {

    @Mock
    private Metadata metadata;

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        parseContext = new ParseContext();
    }

    @Test
    public void testIsEmpty_WhenContextAndJsonConfigsAreEmpty() {
        assertTrue(parseContext.isEmpty());
    }

    @Test
    public void testIsEmpty_WhenContextIsNotEmpty() {
        parseContext.set(String.class, "testValue");
        assertFalse(parseContext.isEmpty());
    }

    @Test
    public void testIsEmpty_WhenJsonConfigsIsNotEmpty() {
        JsonConfig mockJsonConfig = mock(JsonConfig.class);
        parseContext.setJsonConfig("testKey", mockJsonConfig);
        assertFalse(parseContext.isEmpty());
    }

    @Test
    public void testIsEmpty_WhenBothContextAndJsonConfigsAreNotEmpty() {
        parseContext.set(String.class, "testValue");
        JsonConfig mockJsonConfig = mock(JsonConfig.class);
        parseContext.setJsonConfig("testKey", mockJsonConfig);
        assertFalse(parseContext.isEmpty());
    }
}
