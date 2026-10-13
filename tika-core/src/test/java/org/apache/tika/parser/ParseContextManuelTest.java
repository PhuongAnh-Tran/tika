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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import org.apache.tika.config.JsonConfig;

// Tests écrits manuellement pour les mutants non détectés par les tests originaux ni par les tests générés
public class ParseContextManuelTest {

    // Vérifie que setResolvedConfig réutilise la même map et n'écrase pas les entrées existantes
    // lorsqu'on enregistre deux clés différentes (mutant de la ligne 202)
    @Test
    public void testSetResolvedConfigKeepsPreviousEntries() {
        ParseContext context = new ParseContext();
        Object first = new Object();
        Object second = new Object();

        context.setResolvedConfig("a", first);
        context.setResolvedConfig("b", second);

        assertSame(first, context.getResolvedConfig("a"));
        assertSame(second, context.getResolvedConfig("b"));
    }

    // Vérifie que la suppression d'une config JSON supprime aussi la config résolue correspondante
    // (mutant de la ligne 126)
    @Test
    public void testSetJsonConfigNullRemovesResolvedConfig() {
        // initialisation
        ParseContext context = new ParseContext();

        context.setJsonConfig("test", "{\"a\":1}");
        context.setResolvedConfig("test", new Object());

        // le cast lève l'ambiguïté entre setJsonConfig(String, JsonConfig) et setJsonConfig(String, String)
        context.setJsonConfig("test", (JsonConfig) null);

        assertNull(context.getJsonConfig("test"));
        assertFalse(context.hasJsonConfig("test"));
        assertNull(context.getResolvedConfig("test"));
    }

    // Vérifie que hashCode combine les deux maps selon la formule 31 * hash(context) + hash(jsonConfigs)
    // (mutants des lignes 322 et 323)
    @Test
    public void testHashCodeCombinesContextAndJsonConfigs() {
        // initialisation
        ParseContext context = new ParseContext();

        context.set(String.class, "test");
        context.setJsonConfig("test", "{\"a\":1}");

        int hashContext = context.getContextMap().hashCode();
        int hashJsonConfig = context.getJsonConfigs().hashCode();
        // formule de calcul du hashCode de ParseContext
        int hashExpected = 31 * hashContext + hashJsonConfig;

        // garde-fou : une valeur attendue nulle laisserait survivre le mutant "retourne 0"
        assertNotEquals(0, hashExpected);

        assertEquals(hashExpected, context.hashCode());
    }
}
