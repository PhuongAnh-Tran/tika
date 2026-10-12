# IFT3913 — Tâche 2

## Membres de l'équipe

- Ahmed Mami
- Phuong Anh Tran

---

# Classe 1 — `MediaType`

## 1. Choix de la classe

La classe sélectionnée est :

`org.apache.tika.mime.MediaType`

Elle se trouve dans le module :

`tika-core`

Chemin :

`src/main/java/org/apache/tika/mime/MediaType.java`

Une classe de tests existait déjà :

`src/test/java/org/apache/tika/mime/MediaTypeTest.java`

Cette classe a été choisie puisqu'elle possède déjà plusieurs tests, mais n'atteint pas une couverture de 100 %.

Avec JaCoCo, la couverture initiale observée pour `MediaType` était d'environ :

- 79 % des instructions ;
- 74 % des branches.

La classe contenait donc encore plusieurs portions de code non couvertes et des mutants non détectés, ce qui en faisait un bon candidat pour la génération de tests supplémentaires.

---

## 2. Configuration de ChatUniTest

ChatUniTest 2.1.1 a été intégré au projet avec Maven.

Le modèle utilisé est :

**Qwen2.5-Coder 7B**

Le modèle est exécuté localement avec **Ollama**.

ChatUniTest 2.1.1 ne reconnaît cependant qu'un ensemble limité de noms de modèles. Le nom :

`qwen2.5-coder:7b`

n'était donc pas accepté directement.

Pour contourner cette limitation, un alias local a été créé avec Ollama :

```powershell
ollama cp qwen2.5-coder:7b code-llama
```

La configuration ChatUniTest contient donc :

```xml
<model>code-llama</model>
```

mais le modèle réellement exécuté par Ollama est **Qwen2.5-Coder 7B**.

La configuration utilisée pour la génération était notamment :

```xml
<testNumber>1</testNumber>
<maxRounds>2</maxRounds>
<thread>false</thread>
```

La génération sur la classe `MediaType` a pris environ **19 minutes 37 secondes**.

ChatUniTest a traité **11 méthodes** de la classe.

---

## 3. Résultat de la génération de tests

ChatUniTest a généré plusieurs candidats de tests.

Cependant, les fichiers Java finaux n'ont pas été conservés automatiquement dans le dossier de sortie principal puisque plusieurs tests générés ne compilaient pas après les rondes de correction automatique.

Les traces de génération ont été enregistrées dans le dossier :

`chatunitest-info`

Un des candidats générés concernait la méthode :

`compareTo()`

ChatUniTest avait notamment généré des assertions de la forme :

```java
assertEquals(-1, mediaType1.compareTo(mediaType3));
assertEquals(1, mediaType3.compareTo(mediaType1));
```

Deux problèmes principaux ont été observés.

### 3.1 Imports incorrects

ChatUniTest ajoutait automatiquement plusieurs imports Mockito, alors que Mockito n'était pas disponible dans le classpath du module.

Cela empêchait la compilation du test.

### 3.2 Oracle trop spécifique

Le test généré vérifiait des valeurs exactes de `-1` et `1`.

Or, le contrat de `Comparable` ne garantit pas ces valeurs précises. Il garantit seulement qu'une comparaison retourne :

- une valeur négative ;
- zéro ;
- une valeur positive.

Certaines directions de comparaison proposées par le test généré étaient également incorrectes.

---

## 4. Corrections manuelles apportées au test généré

Deux principales corrections ont été nécessaires :

1. suppression des imports Mockito inutiles ;
2. correction des assertions de `compareTo()` afin de vérifier le signe du résultat plutôt qu'une valeur exacte.

Le test corrigé est contenu dans :

`MediaTypeCompareToGeneratedTest.java`

Exemple :

```java
assertTrue(textPlain.compareTo(applicationXml) > 0);
assertTrue(applicationXml.compareTo(textPlain) < 0);
assertTrue(textPlain.compareTo(textHtml) > 0);
assertTrue(textHtml.compareTo(textPlain) < 0);
```

Des tests ont également été conservés pour :

- la comparaison de deux types identiques ;
- la comparaison avec `null`.

Après ces corrections, les tests compilent et s'exécutent correctement.

---

## 5. Comparaison qualitative des oracles

L'oracle proposé par ChatUniTest pour `compareTo()` était trop lié à une valeur numérique précise.

Par exemple :

```java
assertEquals(-1, ...);
```

Cette vérification dépend d'un détail d'implémentation de `String.compareTo()` qui ne fait pas partie du contrat de `Comparable`.

L'oracle corrigé utilise plutôt :

```java
assertTrue(result < 0);
assertTrue(result > 0);
```

Cette forme vérifie directement la propriété fonctionnelle attendue.

L'oracle corrigé est donc moins fragile et correspond mieux au comportement attendu de la méthode.

Les tests écrits manuellement dans le projet Apache Tika utilisent généralement des vérifications centrées sur le comportement observable, alors que l'oracle généré par l'IA était ici trop spécifique.

---

## 6. Analyse de mutation avec les tests originaux

PITest a été ajouté au projet et exécuté sur la classe :

`MediaType`

Avec uniquement la classe de tests originale :

`MediaTypeTest`

les résultats étaient :

| Mesure | Résultat |
|---|---:|
| Mutants générés | 85 |
| Mutants tués | 50 |
| Mutation coverage | 59 % |
| Line coverage PITest | 68 % |
| Test strength | 96 % |
| Mutants sans couverture | 33 |

Les tests originaux tuaient donc :

**50 mutants sur 85**

---

## 7. Analyse après ajout du test dérivé de ChatUniTest

Après ajout de :

`MediaTypeCompareToGeneratedTest`

les résultats sont devenus :

| Mesure | Tests originaux | Après ChatUniTest |
|---|---:|---:|
| Mutants tués | 50/85 | 51/85 |
| Mutation coverage | 59 % | 60 % |
| Line coverage | 68 % | 69 % |
| Mutants sans couverture | 33 | 32 |

Le test généré puis corrigé a donc permis de tuer **un mutant supplémentaire**.

Le mutant concerné se trouvait dans :

`compareTo()`

PITest remplaçait :

```java
return string.compareTo(that.string);
```

par :

```java
return 0;
```

Avant l'ajout du nouveau test, ce mutant était marqué :

`NO_COVERAGE`

Après ajout de :

`testCompareToDifferentMediaTypes()`

le mutant est marqué :

`KILLED`

Le test vérifie en effet que deux types MIME différents donnent un résultat strictement positif ou négatif. Si `compareTo()` retourne toujours `0`, le test échoue.

---

## 8. Tests manuels supplémentaires

Les tests générés ne détectaient pas tous les mutants.

Des tests manuels ont donc été ajoutés dans :

`MediaTypeManualMutationTest.java`

### 8.1 `testSimpleMediaTypeIsCached()`

```java
@Test
public void testSimpleMediaTypeIsCached() {
    String value = "application/x-manual-mutation-test";

    MediaType first = MediaType.parse(value);
    MediaType second = MediaType.parse(value);

    assertNotNull(first);
    assertSame(first, second);
}
```

**Intention :** vérifier que deux appels successifs à `MediaType.parse()` avec un type MIME simple utilisent la mise en cache interne de la classe.

**Motivation des données :** la valeur `application/x-manual-mutation-test` est un type MIME valide et suffisamment spécifique pour réduire le risque qu'elle soit déjà présente dans le cache à cause d'un autre test.

**Oracle :** `assertSame()` est utilisé plutôt que `assertEquals()` afin de vérifier que les deux appels retournent exactement la même instance Java.

Ce test ciblait notamment deux mutants survivants associés à `parse()` et `isSimpleName()`.

Cependant, ces mutants ont continué à survivre après l'ajout du test.

---

### 8.2 `testApplicationFactory()`

```java
@Test
public void testApplicationFactory() {
    MediaType result = MediaType.application("pdf");

    assertEquals("application", result.getType());
    assertEquals("pdf", result.getSubtype());
}
```

**Intention :** vérifier que `MediaType.application("pdf")` construit correctement le type MIME `application/pdf`.

**Motivation des données :** `pdf` constitue un sous-type simple, valide et facilement interprétable.

**Oracle :** le test vérifie séparément :

- le type, qui doit être `"application"` ;
- le sous-type, qui doit être `"pdf"`.

Ce test a permis de tuer quatre mutants supplémentaires :

- remplacement de la valeur retournée par `application()` par `null` ;
- remplacement du résultat de `getType()` par une chaîne vide ;
- modification du calcul d'index dans `getSubtype()` ;
- remplacement du résultat de `getSubtype()` par une chaîne vide.

Le score est passé de :

**51/85 à 55/85**

soit de :

**60 % à 65 %**

---

### 8.3 `testHasParameters()`

```java
@Test
public void testHasParameters() {
    MediaType withoutParams = MediaType.parse("text/plain");
    MediaType withParams = MediaType.parse("text/plain; charset=UTF-8");

    assertFalse(withoutParams.hasParameters());
    assertTrue(withParams.hasParameters());
}
```

**Intention :** vérifier les deux comportements possibles de `hasParameters()`.

**Motivation des données :**

- `text/plain`, sans paramètre ;
- `text/plain; charset=UTF-8`, avec paramètre.

**Oracle :**

- un type sans paramètre doit retourner `false` ;
- un type contenant un paramètre doit retourner `true`.

Ce test a permis de tuer **deux mutants supplémentaires**.

Le score est passé de :

**55/85 à 57/85**

soit de :

**65 % à 67 %**

---

### 8.4 `testEqualsAndHashCode()`

```java
@Test
public void testEqualsAndHashCode() {
    MediaType first = MediaType.parse("application/pdf");
    MediaType same = MediaType.parse("application/pdf");
    MediaType different = MediaType.parse("text/plain");

    assertEquals(first, same);
    assertEquals(first.hashCode(), same.hashCode());
    assertFalse(first.equals(different));
}
```

**Intention :** vérifier le contrat des méthodes `equals()` et `hashCode()`.

**Motivation des données :**

Deux instances représentant `application/pdf` permettent de tester le cas d'égalité.

`text/plain` représente un type différent et permet de vérifier le cas négatif.

**Oracle :**

Deux objets égaux doivent :

- être considérés comme égaux par `equals()` ;
- posséder le même `hashCode()`.

Deux types MIME différents ne doivent pas être considérés comme égaux.

Ce test a permis de tuer **trois mutants supplémentaires**.

Le score est passé de :

**57/85 à 60/85**

soit de :

**67 % à 71 %**

---

## 9. Résultat final

Les résultats obtenus pour `MediaType` sont les suivants :

| Mesure | Tests originaux | + ChatUniTest | Final |
|---|---:|---:|---:|
| Mutants générés | 85 | 85 | 85 |
| Mutants tués | 50 | 51 | 60 |
| Mutation coverage | 59 % | 60 % | 71 % |
| Line coverage PITest | 68 % | 69 % | 74 % |
| Mutants sans couverture | 33 | 32 | 22 |

Au total, les tests ajoutés ont permis de passer de :

**50 à 60 mutants tués**

et de :

**59 % à 71 % de mutation coverage**

Le nombre total de mutants générés reste identique. Les nouveaux tests permettent simplement de détecter davantage de mutations.

---

## 10. Mutants survivants

Deux mutants particulièrement intéressants restent survivants dans la logique liée à l'optimisation des types MIME simples.

Le premier remplace une addition par une soustraction dans :

```java
string.substring(slash + 1)
```

Le second force un retour de `false` dans la logique de :

`isSimpleName()`

Ces mutations peuvent empêcher l'utilisation du chemin optimisé de mise en cache.

Cependant, le chemin général de parsing peut toujours construire un objet `MediaType` fonctionnellement équivalent.

Un test manuel utilisant :

```java
assertSame(first, second);
```

a été ajouté afin de tenter de détecter cette différence.

Malgré cela, PITest indique que les deux mutants survivent toujours.

Ces mutants semblent donc avoir peu d'impact observable sur le comportement public testé de la classe, ou leur détection est rendue difficile par l'état statique du cache lors de l'exécution de PITest.

Ils ont été documentés plutôt que de créer des tests artificiels fortement dépendants de l'implémentation interne.

---

## 11. Exécution

Les tests ajoutés ont été exécutés localement avec Maven.

Exemple :

```powershell
mvn "-Dtest=MediaTypeManualMutationTest" "-Dcheckstyle.skip=true" "-Dspotless.apply.skip=true" test
```

L'analyse de mutation a été exécutée avec :

```powershell
mvn "-Dspotless.apply.skip=true" pitest:mutationCoverage
```

Les tests ont été exécutés avec succès localement.

La validation finale dans GitHub Actions doit permettre de confirmer que tous les nouveaux tests s'exécutent correctement dans le pipeline du projet.

---

# Classe 2 — Partie d'Ahmed Mami

> À compléter par Ahmed avec la classe et les méthodes qu'il a analysées.

---

# Déclaration d'utilisation de l'intelligence artificielle générative

Des outils d'intelligence artificielle générative ont été utilisés dans le cadre de cette tâche.

## Outils utilisés

- **ChatUniTest 2.1.1**, avec **Qwen2.5-Coder 7B** exécuté localement via **Ollama**, pour générer automatiquement des tests unitaires conformément aux consignes de la tâche.
- **ChatGPT (OpenAI)**, utilisé comme outil d'assistance pendant la réalisation du travail.

## Utilisation de ChatUniTest

ChatUniTest a été utilisé pour générer des tests supplémentaires pour les classes choisies.

Les résultats générés ont été inspectés, testés et corrigés lorsque nécessaire avant leur intégration.

Dans le cas de `MediaType`, certains tests générés ne compilaient pas automatiquement. Des imports inutiles ont dû être supprimés et certains oracles ont dû être corrigés.

Aucun test généré n'a été accepté sans vérification.

## Utilisation de ChatGPT

ChatGPT a été utilisé pour :

- aider à comprendre certaines consignes de la tâche ;
- aider au dépannage de la configuration de Maven, ChatUniTest, Ollama et PITest ;
- aider à interpréter les résultats des rapports JaCoCo et PITest ;
- suggérer des pistes de tests supplémentaires à partir des mutants non détectés ;
- aider à analyser les raisons pour lesquelles certains mutants survivaient ;
- aider à structurer, reformuler et réviser la documentation de ce README.

Le code, les tests, les résultats d'exécution et les conclusions ont été vérifiés avant leur intégration au travail.

Les suggestions générées par l'IA ont été adaptées lorsque nécessaire. Certaines propositions ont notamment été rejetées ou corrigées lorsqu'elles ne correspondaient pas au comportement réel du programme.

Aucune information personnelle, sensible ou confidentielle n'a été partagée avec les outils d'IA dans le cadre de cette tâche.

---
Welcome to Apache Tika  <https://tika.apache.org/>
=================================================

[![license](https://img.shields.io/github/license/apache/tika.svg?maxAge=2592000)](http://www.apache.org/licenses/LICENSE-2.0)
[![Jenkins](https://img.shields.io/jenkins/s/https/ci-builds.apache.org/job/Tika/job/tika-main-jdk17.svg?maxAge=3600)](https://ci-builds.apache.org/job/Tika/job/tika-main-jdk17/)
[![Jenkins tests](https://img.shields.io/jenkins/t/https/ci-builds.apache.org/job/Tika/job/tika-main-jdk17.svg?maxAge=3600)](https://ci-builds.apache.org/job/Tika/job/tika-main-jdk17/lastBuild/testReport/)
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.tika/tika.svg?maxAge=86400)](http://search.maven.org/#search|ga|1|g%3A%22org.apache.tika%22)

Apache Tika(TM) is a toolkit for detecting and extracting metadata and structured text content from various documents using existing parser libraries.

Tika is a project of the [Apache Software Foundation](https://www.apache.org).

Apache Tika, Tika, Apache, the Apache feather logo, and the Apache Tika project logo are trademarks of The Apache Software Foundation.

Quick Start
===========

**Parse a file in Java:**

```java
import org.apache.tika.Tika;

Tika tika = new Tika();
String text = tika.parseToString(new File("document.pdf"));
System.out.println(text);
```

**From the command line:**

```bash
java -jar tika-app-*.jar --text document.pdf
```

**Maven dependency:**

```xml
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers-standard-package</artifactId>
    <version>4.x.y</version>
    <type>pom</type>
</dependency>
```

Getting Started
===============
Pre-built binaries of Apache Tika standalone applications are available
from https://tika.apache.org/download.html . Pre-built binaries of all the
Tika jars can be fetched from Maven Central or your favourite Maven mirror.

**Tika 2.X and support for Java 8 reached End of Life (EOL) in April, 2025. 
See [Tika Roadmap 2.x, 3.x and beyond](https://cwiki.apache.org/confluence/display/TIKA/Tika+Roadmap+--+2.x%2C+3.x+and+Beyond).** 

Tika is based on **Java 17** and uses the [Maven 3](https://maven.apache.org) build system.
**N.B.** [Docker](https://www.docker.com/products/personal) is used for tests in tika-integration-tests. If Docker is not installed, those tests are skipped.

To build Tika from source, use the following command in the main directory:

    ./mvnw clean install

The Maven wrapper (`mvnw`) is included in the repository and will automatically download
the correct Maven version if needed. On Windows, use `mvnw.cmd` instead.

The build consists of a number of components, including a standalone runnable jar that you can use to try out Tika features. You can run it like this:

    java -jar tika-app/target/tika-app-*.jar --help


To build a specific project (for example, tika-server-standard):

    ./mvnw clean install -am -pl :tika-server-standard

If the ossindex-maven-plugin is causing the build to fail because a dependency
has now been discovered to have a vulnerability:

    ./mvnw clean install -Dossindex.skip


Faster Builds
=============

**Fast profile** - Use `-Pfast` to skip tests, checkstyle, and spotless:

    ./mvnw clean install -Pfast

**Parallel builds** - Add `-T1C` to build with 1 thread per CPU core:

    ./mvnw clean install -Pfast -T1C

**Maven Daemon (mvnd)** - Keeps a warm JVM running for 2-3x faster rebuilds:

```bash
# Install: https://github.com/apache/maven-mvnd
# macOS: brew install mvndaemon/tap/mvnd

# Use exactly like mvn
mvnd clean install -Pfast
mvnd test -pl :tika-core
```

**Combine both** for maximum speed during development:

    mvnd clean install -Pfast -T1C


Reproducible Builds
===================

Apache Tika supports [reproducible builds](https://reproducible-builds.org/). This means
that building the same source code with the same JDK version should produce
byte-for-byte identical artifacts, regardless of the build machine or time.

Key configuration:
- `project.build.outputTimestamp` is set in `tika-parent/pom.xml`
- All Maven plugins are configured to produce deterministic output

To verify the build plan supports reproducibility:

    ./mvnw artifact:check-buildplan

To verify two builds produce identical artifacts:

    ./mvnw clean install -DskipTests
    mv ~/.m2/repository/org/apache/tika tika-build-1
    ./mvnw clean install -DskipTests
    diff -r tika-build-1 ~/.m2/repository/org/apache/tika


Maven Dependencies
==================

Apache Tika provides *Bill of Material* (BOM) artifact to align Tika module versions and simplify version management. 
To avoid convergence errors in your own project, import this
bom or Tika's parent pom.xml in your dependency management section.

If you use Apache Maven:

```xml
<project>
  <dependencyManagement>
    <dependencies>
      <dependency>
       <groupId>org.apache.tika</groupId>
       <artifactId>tika-bom</artifactId>
       <version>4.x.y</version>
       <type>pom</type>
       <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.apache.tika</groupId>
      <artifactId>tika-parsers-standard-package</artifactId>
      <type>pom</type>
      <!-- version not required since BOM included -->
    </dependency>
  </dependencies>
</project>
```

For Gradle:

```kotlin
dependencies {
  implementation(platform("org.apache.tika:tika-bom:4.x.y"))

  // version not required since bom (platform in Gradle terms)
  implementation("org.apache.tika:tika-parsers-standard-package@pom")
}
```

Migrating to 4.x
================
TBD

Contributing
============
See [CONTRIBUTING.md](CONTRIBUTING.md) and https://tika.apache.org/contribute.html

[![contributors](https://contributors-img.web.app/image?repo=apache/tika)](https://github.com/apache/tika/graphs/contributors)

Building from a Specific Tag
============================
Let's assume that you want to build the 3.0.1 tag:
```
0. Download and install hub.github.com
1. git clone https://github.com/apache/tika.git
2. cd tika
3. git checkout 3.0.1
4. ./mvnw clean install
```

If a new vulnerability has been discovered between the date of the
tag and the date you are building the tag, you may need to build with:

```
4. ./mvnw clean install -Dossindex.skip
```

If a local test is not working in your environment, please notify
 the project at dev@tika.apache.org. As an immediate workaround,
 you can turn off individual tests with e.g.:

```
4. ./mvnw clean install -Dossindex.skip -Dtest=\!UnpackerResourceTest#testPDFImages
```

License (see also LICENSE.txt)
==============================

Collective work: Copyright 2011 The Apache Software Foundation.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.

Apache Tika includes a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the licenses listed in the LICENSE.txt file.

Export Control
==============

This distribution includes cryptographic software.  The country in which you currently reside may have restrictions on the import, possession, use, and/or re-export to another country, of encryption software.  BEFORE using any encryption software, please  check your country's laws, regulations and policies concerning the import, possession, or use, and re-export of encryption software, to  see if this is permitted.  See <http://www.wassenaar.org/> for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS), has classified this software as Export Commodity Control Number (ECCN) 5D002.C.1, which includes information security software using or performing cryptographic functions with asymmetric algorithms.  The form and manner of this Apache Software Foundation distribution makes it eligible for export under the License Exception ENC Technology Software Unrestricted (TSU) exception (see the BIS Export Administration Regulations, Section 740.13) for both object code and source code.

The following provides more details on the included cryptographic software:

Apache Tika uses the Bouncy Castle generic encryption libraries for extracting text content and metadata from encrypted PDF files.  See <http://www.bouncycastle.org/> for more details on Bouncy Castle.  

Mailing Lists
=============

* user@tika.apache.org - About using Tika
* dev@tika.apache.org - About developing Tika

Subscribe by sending a message to `{list}-subscribe@tika.apache.org`.

Issue Tracker
=============

https://issues.apache.org/jira/browse/TIKA

Security
========

See [SECURITY.md](SECURITY.md) and https://tika.apache.org/security.html
