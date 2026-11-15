
# Tâche 3 — Vulnérabilités introduites dans Apache Tika

## Objectif et portée

Introduire **7 types différents de faiblesses de sécurité détectables par Semgrep** dans une copie d'Apache Tika et comparer les résultats avant et après les modifications.

- Dépôt : https://github.com/PhuongAnh-Tran/tika
- Branche : `tache3-vulnerabilites`
- Commit de départ : `a2d75c2c10563b261148e9bafe4046832b33c1a6`
- Fichiers modifiés : `DigestHelper.java`, `NetworkParser.java`, `XMLReaderUtils.java` et `H2Util.java`.

Les ajouts sont destinés uniquement à l'exercice. Le cas MD5 est activable explicitement; les six autres exemples sont des méthodes de démonstration non appelées par le fonctionnement habituel de Tika. **Les détections Semgrep ne prouvent pas que les fonctionnalités normales de Tika sont exploitables.**

## Configuration et comparaison Semgrep

- Version : **Semgrep 1.185.0**
- Règles : **`p/java`** (60 règles Java au moment des analyses)
- Périmètre : dépôt Tika, fichiers suivis par Git, avec les exclusions Semgrep habituelles.
- Rapport initial : `semgrep_avant_nouveau.json`
- Rapport final : `semgrep_apres_final.json`

Commande initiale :

```bash
semgrep scan --config p/java --metrics=off --json --output semgrep_avant_nouveau.json .
```

Commande finale :

```bash
semgrep scan --config p/java --metrics=off --json --output semgrep_apres_final.json .
```

**Résultats : 11 détections avant, 19 après, soit 8 nouvelles détections réparties en 7 types différents.**

Les deux scans portent sur 1 468 fichiers Java. La différence a été vérifiée à partir des identifiants de règles, des chemins des fichiers et des emplacements des détections.

Deux règles signalent le même usage d'AES-ECB : cela compte pour **un seul type** de vulnérabilité.

## Vulnérabilités introduites

### 1. Hachage faible : MD5

- **Fichier :** `tika-core/src/main/java/org/apache/tika/digest/DigestHelper.java`, ligne **109**.
- **Pourquoi ce fichier :** `DigestHelper` calcule les empreintes de flux pendant l'analyse de documents.
- **Modification :** ajout d'une branche activable par un marqueur `ParseContext`, qui calcule `MessageDigest.getInstance("MD5")` et conserve l'empreinte dans les métadonnées.
- **Risque :** MD5 ne résiste pas suffisamment aux collisions pour les usages de sécurité qui exigent cette propriété.
- **Semgrep :** `java.lang.security.audit.crypto.use-of-md5.use-of-md5` — **1 nouvelle détection**.

### 2. Chiffrement AES en mode ECB

- **Fichier :** `tika-core/src/main/java/org/apache/tika/digest/DigestHelper.java`, ligne **155**.
- **Pourquoi ce fichier :** la classe traite déjà des opérations cryptographiques liées aux empreintes de documents; elle sert ici de support à la démonstration.
- **Modification :** ajout de `ift3913DemoEcb`, qui initialise `Cipher` avec `AES/ECB/NoPadding`.
- **Risque :** ECB chiffre chaque bloc indépendamment; des blocs identiques peuvent révéler des motifs. Ce mode ne fournit pas non plus d'authentification.
- **Semgrep :**
  - `java.lang.security.audit.crypto.ecb-cipher.ecb-cipher`
  - `java.lang.security.audit.crypto.use-of-aes-ecb.use-of-aes-ecb`
- **Résultat :** 2 nouvelles détections, mais 1 seul type de vulnérabilité.

### 3. Réutilisation d'un nonce GCM

- **Fichier :** `tika-core/src/main/java/org/apache/tika/digest/DigestHelper.java`, ligne **166**.
- **Pourquoi ce fichier :** même contexte de démonstration cryptographique que l'exemple AES-ECB.
- **Modification :** ajout de `ift3913DemoGcm`, qui utilise un nonce constant avec `AES/GCM/NoPadding`.
- **Risque :** réutiliser un nonce avec la même clé GCM compromet les garanties de confidentialité et d'authenticité.
- **Semgrep :** `java.lang.security.audit.crypto.gcm-nonce-reuse.gcm-nonce-reuse` — **1 nouvelle détection**.

### 4. Communication réseau non chiffrée

- **Fichier :** `tika-core/src/main/java/org/apache/tika/parser/NetworkParser.java`, ligne **180**.
- **Pourquoi ce fichier :** `NetworkParser` contient déjà du code de communication réseau.
- **Modification :** ajout de `ift3913DemoPlainSocket`, qui crée une connexion avec `new Socket(host, port)` sans protection TLS.
- **Risque :** si des données sensibles circulent sur cette connexion sans protection supplémentaire, elles peuvent être interceptées ou modifiées.
- **Semgrep :** `java.lang.security.audit.crypto.unencrypted-socket.unencrypted-socket` — **1 nouvelle détection**.

Une autre occurrence de cette règle, à la ligne 71, existait déjà avant les modifications. Elle n'est donc pas comptée comme une nouvelle détection.

### 5. Version obsolète de TLS

- **Fichier :** `tika-core/src/main/java/org/apache/tika/parser/NetworkParser.java`, ligne **189**.
- **Pourquoi ce fichier :** la configuration du protocole de transport relève des communications réseau.
- **Modification :** ajout de `ift3913DemoWeakTls`, qui demande `SSLContext.getInstance("TLSv1")`.
- **Risque :** TLS 1.0 est obsolète et n'offre pas les garanties attendues d'une configuration TLS moderne.
- **Semgrep :** `java.lang.security.audit.weak-ssl-context.weak-ssl-context` — **1 nouvelle détection**.

### 6. Analyse XML sans protections XXE explicites

- **Fichier :** `tika-core/src/main/java/org/apache/tika/utils/XMLReaderUtils.java`, ligne **1314**.
- **Pourquoi ce fichier :** cette classe centralise la création et la configuration des analyseurs XML de Tika.
- **Modification :** ajout de `ift3913DemoUnsafeXml`, qui construit un `DocumentBuilderFactory` puis un `DocumentBuilder` sans désactiver explicitement les entités externes.
- **Risque :** suivant la configuration effective du parseur, un document XML malveillant peut déclencher la résolution d'entités externes (XXE), avec des conséquences possibles sur les fichiers locaux ou les accès réseau.
- **Semgrep :** `java.lang.security.audit.xxe.documentbuilderfactory-disallow-doctype-decl-missing.documentbuilderfactory-disallow-doctype-decl-missing` — **1 nouvelle détection**.

Une autre détection liée aux XXE existait déjà à la ligne 307. Elle n'est pas comptée comme nouvelle.

### 7. Construction non sûre d'une requête SQL

- **Fichier :** `tika-eval/tika-eval-app/src/main/java/org/apache/tika/eval/app/db/H2Util.java`, ligne **101**.
- **Pourquoi ce fichier :** `H2Util` contient déjà les opérations JDBC liées à la base H2 utilisée par Tika Eval.
- **Modification :** ajout de `ift3913DemoUnsafeSql`, qui concatène un nom de table fourni en paramètre dans `SELECT * FROM ...` et exécute la requête avec `Statement`.
- **Risque :** sans validation stricte du nom de table, la concaténation peut permettre de modifier la requête SQL exécutée.
- **Semgrep :** `java.lang.security.audit.formatted-sql-string.formatted-sql-string` — **1 nouvelle détection**.

Une occurrence à la ligne 70 existait déjà avant les modifications. Elle n'est pas comptée comme nouvelle.

## Validation

### Analyse Semgrep

L'analyse finale a été exécutée avec succès.

- Avant les modifications : **11 détections**
- Après les modifications : **19 détections**
- Différence : **8 nouvelles détections**
- Types de vulnérabilités représentés : **7**
- Règles Java utilisées : **60**
- Fichiers Java analysés : **1 468**

Les nouvelles détections ont été identifiées en comparant les identifiants de règles, les chemins et les emplacements des résultats des deux rapports JSON.

### Compilation Maven

Commande exécutée :

```bash
./mvnw -pl tika-core,tika-eval/tika-eval-app -am -DskipTests -Dcheckstyle.skip=true test-compile
```

**Résultat : `BUILD SUCCESS`**

Les sources principales et les sources de test des modules concernés et de leurs dépendances ont été compilées avec succès.

### Vérification Git

La commande suivante a été exécutée :

```bash
git diff --cached --check
```

Aucune erreur n'a été signalée.

### Limites de la validation

- Les tests unitaires n'ont pas été exécutés (`-DskipTests`).
- Checkstyle a été désactivé pour la compilation (`-Dcheckstyle.skip=true`).
- Une exécution antérieure de Checkstyle avait signalé 59 erreurs qui n'ont pas été analysées dans cette validation.
- Semgrep a ignoré certains fichiers selon ses règles d'exclusion et sa limite de taille. Le scan indiquait environ 99,9 % des lignes analysées.
- Le paquet `p/java` peut évoluer au fil du temps; les résultats correspondent à la configuration utilisée lors de l'exercice.
- Les exemples introduits sont isolés ou activables explicitement. Ils montrent des motifs de code considérés comme dangereux par analyse statique, sans démontrer leur exploitation dans le fonctionnement normal de Tika.

## Utilisation de l'IA

ChatGPT a été utilisé pour proposer des modifications Java, interpréter les résultats Semgrep et aider à rédiger la documentation.

Les modifications ont été appliquées par les auteurs. Les détections Semgrep et la compilation Maven ont été vérifiées dans leur environnement.

Les validations non exécutées, notamment les tests unitaires et Checkstyle complet, ne sont pas présentées comme réussies.
