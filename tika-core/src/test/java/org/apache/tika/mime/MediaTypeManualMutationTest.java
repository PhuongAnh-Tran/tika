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
package org.apache.tika.mime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MediaTypeManualMutationTest {

    @Test
    public void testSimpleMediaTypeIsCached() {
        String value = "application/x-manual-mutation-test";

        MediaType first = MediaType.parse(value);
        MediaType second = MediaType.parse(value);

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    public void testApplicationFactory() {
        MediaType result = MediaType.application("pdf");

        assertEquals("application", result.getType());
        assertEquals("pdf", result.getSubtype());
    }

    @Test
    public void testHasParameters() {
        MediaType withoutParams = MediaType.parse("text/plain");
        MediaType withParams = MediaType.parse("text/plain; charset=UTF-8");

        assertFalse(withoutParams.hasParameters());
        assertTrue(withParams.hasParameters());
    }

    @Test
    public void testEqualsAndHashCode() {
        MediaType first = MediaType.parse("application/pdf");
        MediaType same = MediaType.parse("application/pdf");
        MediaType different = MediaType.parse("text/plain");

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertFalse(first.equals(different));
    }
}
