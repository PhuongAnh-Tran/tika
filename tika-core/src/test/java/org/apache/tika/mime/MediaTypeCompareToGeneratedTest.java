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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MediaTypeCompareToGeneratedTest {

    @Test
    public void testCompareToDifferentMediaTypes() {
        MediaType textPlain = new MediaType("text", "plain");
        MediaType textHtml = new MediaType("text", "html");
        MediaType applicationXml = new MediaType("application", "xml");

        assertTrue(textPlain.compareTo(applicationXml) > 0);
        assertTrue(applicationXml.compareTo(textPlain) < 0);

        assertTrue(textPlain.compareTo(textHtml) > 0);
        assertTrue(textHtml.compareTo(textPlain) < 0);
    }

    @Test
    public void testCompareToSameMediaType() {
        MediaType first = new MediaType("text", "plain");
        MediaType second = new MediaType("text", "plain");

        assertEquals(0, first.compareTo(second));
    }

    @Test
    public void testCompareToNull() {
        MediaType mediaType = new MediaType("text", "plain");

        assertThrows(
                NullPointerException.class,
                () -> mediaType.compareTo(null)
        );
    }
}
