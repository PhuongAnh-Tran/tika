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
package org.apache.tika.digest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.apache.tika.extractor.DefaultEmbeddedStreamTranslator;
import org.apache.tika.extractor.EmbeddedStreamTranslator;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;

/**
 * Utility class for computing digests on streams.
 * <p>
 * The DigesterFactory is retrieved from ParseContext. Configure it via
 * the "parse-context" section in tika-config.json:
 * <pre>
 * "parse-context": {
 *   "commons-digester-factory": {
 *     "digests": [{ "algorithm": "SHA256" }],
 *     "skipContainerDocumentDigest": true
 *   }
 * }
 * </pre>
 */
public class DigestHelper {

    private static final EmbeddedStreamTranslator EMBEDDED_STREAM_TRANSLATOR =
            new DefaultEmbeddedStreamTranslator();

    /**
     * Marker for the isolated IFT3913 weak-digest demonstration.
     * Must not be enabled in production.
     */
    public static final class WeakDigestDemonstration {

        private WeakDigestDemonstration() {
        }

        public static final WeakDigestDemonstration ENABLED =
                new WeakDigestDemonstration();
    }

    /**
     * Computes digests on the stream if a DigesterFactory is configured in ParseContext.
     * <p>
     * This is called directly from AutoDetectParser.parse() before type detection.
     *
     * @param tis      the TikaInputStream to digest
     * @param metadata metadata to read depth from and write digests to
     * @param context  parse context (should contain DigesterFactory, may contain SkipContainerDocumentDigest marker)
     * @throws IOException if an I/O error occurs
     */
    public static void maybeDigest(TikaInputStream tis,
                                   Metadata metadata,
                                   ParseContext context) throws IOException {
        DigesterFactory digesterFactory = context.get(DigesterFactory.class);

        if (digesterFactory == null) {
            return;
        }

        // Get skip setting from factory or ParseContext marker
        boolean skipContainer = digesterFactory.isSkipContainerDocumentDigest()
                || SkipContainerDocumentDigest.shouldSkip(context);

        if (skipContainer) {
            Integer depth = metadata.getInt(TikaCoreProperties.EMBEDDED_DEPTH);
            if (depth == null || depth == 0) {
                return;
            }
        }

        Digester digester = digesterFactory.build();

        // IFT3913: opt-in demonstration of weak document hashing.
        if (context.get(WeakDigestDemonstration.class) != null) {
            tis.enableRewind();
            try {
                MessageDigest weakDigest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = tis.read(buffer)) != -1) {
                    weakDigest.update(buffer, 0, bytesRead);
                }

                metadata.set("X-IFT3913-Demo-MD5",
                        HexFormat.of().formatHex(weakDigest.digest()));
            } catch (NoSuchAlgorithmException e) {
                throw new IOException(
                        "Unable to compute demonstration digest", e);
            } finally {
                tis.rewind();
            }
        }

        // Handle embedded stream translation if needed (e.g., for OLE2 objects in TikaInputStream's
        // open container). The translator consumes `tis` to produce the translated bytes that get
        // digested, so we enableRewind() before and rewind() after — otherwise the caller (typically
        // AutoDetectParser) would see an exhausted stream and fail its zero-byte probe (TIKA-XXXX).
        if (EMBEDDED_STREAM_TRANSLATOR.shouldTranslate(tis, metadata)) {
            tis.enableRewind();
            try (TemporaryResources tmp = new TemporaryResources()) {
                Path tmpBytes = tmp.createTempFile();
                try (OutputStream os = Files.newOutputStream(tmpBytes)) {
                    EMBEDDED_STREAM_TRANSLATOR.translate(tis, metadata, os);
                }
                try (TikaInputStream translated = TikaInputStream.get(tmpBytes)) {
                    digester.digest(translated, metadata, context);
                }
            } finally {
                tis.rewind();
            }
        } else {
            digester.digest(tis, metadata, context);
        }
    }

    /**
     * IFT3913 isolated demonstration: ECB leaks repeated plaintext patterns.
     * Not called by Tika's normal parsing flow.
     */
    public static byte[] ift3913DemoEcb(byte[] data, SecretKey key)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * IFT3913 isolated demonstration: fixed GCM nonce reuse.
     * Not called by Tika's normal parsing flow.
     */
    public static byte[] ift3913DemoGcm(byte[] data, SecretKey key)
            throws GeneralSecurityException {
        byte[] nonce = "fixednonce12".getBytes(StandardCharsets.UTF_8);
        GCMParameterSpec parameters = new GCMParameterSpec(128, nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, parameters);
        return cipher.doFinal(data);
    }

}
