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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParseContext_getResolvedConfig_7_2_Test {

    private ParseContext parseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
    }

    @Test
    public void testGetResolvedConfig_whenResolvedConfigsIsNull() throws Exception {
        // Arrange
        Field resolvedConfigsField = parseContext.getClass().getDeclaredField("resolvedConfigs");
        resolvedConfigsField.setAccessible(true);
        resolvedConfigsField.set(parseContext, null);
        // Act
        Object result = parseContext.getResolvedConfig("testKey");
        // Assert
        assertNull(result);
    }

    @Test
    public void testGetResolvedConfig_whenResolvedConfigsIsNotEmpty() throws Exception {
        // Arrange
        Field resolvedConfigsField = parseContext.getClass().getDeclaredField("resolvedConfigs");
        resolvedConfigsField.setAccessible(true);
        Map<String, Object> resolvedConfigs = new HashMap<>();
        resolvedConfigs.put("testKey", "testValue");
        resolvedConfigsField.set(parseContext, resolvedConfigs);
        // Act
        Object result = parseContext.getResolvedConfig("testKey");
        // Assert
        assertEquals("testValue", result);
    }

    @Test
    public void testGetResolvedConfig_whenResolvedConfigsContainsNull() throws Exception {
        // Arrange
        Field resolvedConfigsField = parseContext.getClass().getDeclaredField("resolvedConfigs");
        resolvedConfigsField.setAccessible(true);
        Map<String, Object> resolvedConfigs = new HashMap<>();
        resolvedConfigs.put("testKey", null);
        resolvedConfigsField.set(parseContext, resolvedConfigs);
        // Act
        Object result = parseContext.getResolvedConfig("testKey");
        // Assert
        assertNull(result);
    }
}
