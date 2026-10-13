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
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.tika.config.JsonConfig;

@ExtendWith(MockitoExtension.class)
public class ParseContext_copyFrom_11_3_Test {

    @Mock
    private JsonConfig mockJsonConfig;
    
    //ajout d'une instace mock JsonConfig pour targetContext
    @Mock 
    private JsonConfig mockJsonConfigTarget;

    private ParseContext sourceContext;

    private ParseContext targetContext;

    private Object initialResolvedConfigSourceObject = new Object();
    private Object initialResolvedConfigTargetObject = new Object();

    @BeforeEach
    public void setUp() {
        sourceContext = new ParseContext();
        targetContext = new ParseContext();

        // Initialize source context with some data
        sourceContext.set(String.class, "testValue");
        sourceContext.setJsonConfig("testKey", mockJsonConfig);

        // sourceContext.setResolvedConfig("testKey", new Object());
        sourceContext.setResolvedConfig("testKey", initialResolvedConfigSourceObject);

        // Initialize target context with some data
        targetContext.set(String.class, "initialValue");
        targetContext.setJsonConfig("initialKey", mockJsonConfigTarget);

        //erreur utilisation
        // targetContext.setResolvedConfig("initialKey", new Object());
        targetContext.setResolvedConfig("initialKey", initialResolvedConfigTargetObject);
        }

    @Test
    public void testCopyFrom() throws Exception {
        // Call the copyFrom method
        targetContext.copyFrom(sourceContext);
       
        //erreur getContextMap() retourne une Map<String,Object> donc ici il faut utiliser getName() pour obtenir la clé de type String
        // assertEquals("testValue", targetContext.getContextMap().get(String.class));   
              
        // Verify that the source context data has been copied to the target context
        assertEquals("testValue", targetContext.getContextMap().get(String.class.getName())); 


        // erreur utilise le meme mockJsonConfig pour target et source ajout d'un mockJsonConfigTarget pour targetContext
        assertSame(mockJsonConfig, targetContext.getJsonConfigs().get("testKey")); 

        // erreur creation d'un objet nouveau et l'utiliser pour la comparaison va fail 100%
        // assertEquals(new Object(), targetContext.getResolvedConfig("testKey"));

        assertSame(initialResolvedConfigSourceObject, targetContext.getResolvedConfig("testKey"));


        // Verify that the target context data has been cleared

        // assertNull(targetContext.getContextMap().get("initialValue")); le test ne fait pas de sens car la cle n'existe pas
        
        // erreur: copyFrom ne supprime pas des cle il ecrase juste les valeurs des clees existantes
        // assertNull(targetContext.getJsonConfigs().get("initialKey"));
        //correction: on verifie que la valeur de la cle n'a pas change
        assertSame(mockJsonConfigTarget, targetContext.getJsonConfigs().get("initialKey"));

        // erreur: copyFrom ne supprime pas des cle il ecrase juste les valeurs des clees existantes
        // assertNull(targetContext.getResolvedConfig("initialKey"));
        //correction: on verifie que la valeur de la cle n'a pas change
        assertSame(initialResolvedConfigTargetObject, targetContext.getResolvedConfig("initialKey"));
    }

    @Test
    public void testCopyFromWithNullSource() throws Exception {
        // Call the copyFrom method with a null source
        targetContext.copyFrom(null);



        //assertEquals("initialValue", targetContext.getContextMap().get("initialValue"));
        //erreur: prend l'objet comme cle

        // Verify that the target context data remains unchanged
        assertEquals("initialValue", targetContext.getContextMap().get(String.class.getName()));



        assertEquals(mockJsonConfigTarget, targetContext.getJsonConfigs().get("initialKey"));

        //erreur: cree un nouvel objet pour la comparaison donc fail 100%
        // assertEquals(new Object(), targetContext.getResolvedConfig("initialKey"));

        assertSame(initialResolvedConfigTargetObject, targetContext.getResolvedConfig("initialKey"));
    }
}
