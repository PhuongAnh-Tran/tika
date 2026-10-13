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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.tika.metadata.Metadata;

@ExtendWith(MockitoExtension.class)
public class ParseContext_get_2_1_Test {

    @Mock
    private Metadata metadata;

    @Test
    public void testGetWithExistingKey() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("testValue", result);
    }

    @Test
    public void testGetWithNonExistingKey() {
        ParseContext parseContext = new ParseContext();
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("defaultValue", result);
    }

    @Test
    public void testGetWithDefaultValue() {
        ParseContext parseContext = new ParseContext();
        String result = parseContext.get(String.class, null);
        assertEquals(null, result);
    }

    @Test
    public void testGetWithNonGenericKey() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("testValue", result);
    }

    @Test
    public void testGetWithNullDefaultValue() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, null);
        assertEquals("testValue", result);
    }

    @Test
    public void testGetWithEmptyKey() {
        ParseContext parseContext = new ParseContext();
        String result = parseContext.get(String.class, "defaultValue");
        assertEquals("defaultValue", result);
    }

    @Test
    public void testGetWithEmptyDefaultValue() {
        ParseContext parseContext = new ParseContext();
        parseContext.set(String.class, "testValue");
        String result = parseContext.get(String.class, "");
        assertEquals("testValue", result);
    }
}
