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

import org.apache.tika.config.JsonConfig;

public class ParseContext_equals_14_4_Test {

    private ParseContext parseContext;

    private ParseContext otherParseContext;

    @BeforeEach
    public void setUp() {
        parseContext = new ParseContext();
        otherParseContext = new ParseContext();
        // Set up initial state for parseContext
        parseContext.set(String.class, "testValue");

        // JsonConfig mockJsonConfig = mock(JsonConfig.class);
        //erreur: on veut prouver l'egalite de entre ParseContext donc utiliser le meme mock
        // deux fois ne montre en rien celle-ci, avec un string on cree un nouvel objet mais avec le meme contenu 
        parseContext.setJsonConfig("testJsonConfig", "mockJsonConfig");
        
        
        // Set up initial state for otherParseContext
        otherParseContext.set(String.class, "testValue");


        // JsonConfig mockJsonConfigOther = mock(JsonConfig.class);
        //erreur: mock cree un JsonConfig different de celui de parseContext il faut reutiliser le meme
        //but pour le test d'egalite, on veut que les deux objets aient le meme JsonConfig



        otherParseContext.setJsonConfig("testJsonConfig", "mockJsonConfig");
    }

    @Test
    public void testEqualsReflexive() {
        assertTrue(parseContext.equals(parseContext));
    }

    @Test
    public void testEqualsSymmetric() { // ce test ne fonctionnait pas car les deux ParseContext avaient des instances differentes de JsonConfig, donc equals retourne false

        assertTrue(parseContext.equals(otherParseContext) && otherParseContext.equals(parseContext));
    }

    @Test
    public void testEqualsTransitive() {
        ParseContext thirdParseContext = new ParseContext();
        thirdParseContext.set(String.class, "testValue");

        // meme probleme ici que dans symetric
        // JsonConfig mockJsonConfigThird = mockJsonConfigOther; // Utiliser le même mock JsonConfig que les deux autres ParseContext
        thirdParseContext.setJsonConfig("testJsonConfig", "mockJsonConfig");

        assertTrue(parseContext.equals(otherParseContext) && otherParseContext.equals(thirdParseContext) && parseContext.equals(thirdParseContext));
    }

    @Test
    public void testEqualsNull() {
        assertFalse(parseContext.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        assertFalse(parseContext.equals("testString"));
    }

    @Test
    public void testEqualsDifferentContext() {
        otherParseContext.set(String.class, "differentValue");
        assertFalse(parseContext.equals(otherParseContext));
    }

    @Test
    public void testEqualsDifferentJsonConfig() {
        otherParseContext.setJsonConfig("testJsonConfig", mock(JsonConfig.class));
        assertFalse(parseContext.equals(otherParseContext));
    }
}
