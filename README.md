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

# Classe 2 — `ParseContext`
## 1. Choix de la classe
`org.apache.tika.parser.ParseContext` du module `tika-core` avec fichier test existant `ParseContextTest.java`.

L'une des raisons du choix est que cette classe possède une couverture de code partielle (44% des instructions) (voir le tableau ci-dessous)
| Métrique | Couvert / total | Taux |
|---|---|---|
| Instructions | 148 / 268 | 44 % |
| Branches | 27 / 40 | 32 % |
| Lignes | 37 / 66 | 56 % |
| Méthodes | 11 / 18 | 61 % |
---
On a aussi avec PITest, une faible couverture de mutation (28%) et un nombre important de mutants survivants (36) (voir le tableau ci-dessous)
| | A : tests d'origine 
|---|---|
| Tués | 12 | 
| Survivants | 3 | 
| Sans couverture | 28 | 
| Total | 43 | 
| Score de mutation (tués / total) | 27.9 % |
| Force des tests (tués / (tués + survivants)) | 80.0 % |

Le dernier point qui a motivé ce choix c'est la faible dépendance de la classe avec d'autres classes du projet seulement 3 y sont utilisées `org.apache.tika.config.JsonConfig`, `org.apache.tika.metadata.Metadata` et `org.apache.tika.metadata.writefilter.MetadataWriteLimiterFactory` ce qui facilite la génération des tests avec ChatUniTest.

## 2. Configuration de ChatUniTest
par rapport à la configuration de ChatUniTest pour la classe `ParseContext` il n'y a pas de modification par rapport à celle de la classe `MediaType` à part pour ce paramètre: 
```xml
          <testNumber>5</testNumber>

```
le nombre de tests qui passe de 1 à 5.

Temps total de la génération: 1h47min

## 3. Résultat de la génération de tests
### 3.1 emplacement des tests générés

Suite à la génération on retrouve les tests produits dans le dossier `chatunitest-tests` dans ``tika-core`` avec le format suivant `ParseContext_<méthode>_<n>_<m>_Test.java`. Au début avec l'utilisation du modèle codeqwen:v1.5:7b la génération n'arrive pas à générer de façon constante des tests pour les méthodes de la classe mais en changeant de modèle pour qwen2.5coder:7b et en ajustant le nombre de tests et round de correction on arrive à cette liste voici tous les fichiers retenus

| Fichier retenu | Méthode ciblée |
|---|---|
| `ParseContext_isEmpty_10_0_Test` | isEmpty |
| `ParseContext_equals_14_4_Test` | equals |
| `ParseContext_toString_16_0_Test` | toString |
| `ParseContext_newMetadata_12_4_Test` | newMetadata |
| `ParseContext_getJsonConfig_5_0_Test` | getJsonConfig |
| `ParseContext_hasJsonConfig_9_0_Test` | hasJsonConfig |
| `ParseContext_get_1_1_Test` | get(Class) |
| `ParseContext_get_2_1_Test` | get(Class, T) |
| `ParseContext_copyFrom_11_3_Test` | copyFrom |
| `ParseContext_getResolvedConfig_7_2_Test` | getResolvedConfig |
---
Aucun test valide n'a été obtenu pour ``hashCode``

### 3.2 correction
les tests tels quels ne compilent pas directement dû à Checkstyle qui impose un certain style comme l'interdiction d'avoir des imports .* ou des imports inutilisés, il faut aussi ajouter la licence Apache au début de chaque fichier.
Ensuite on a des tests qui ne passent pas dû à des erreurs (voir le tableau ci-dessous)
| Test | Ligne | Résultat | Cause (déduite du code) |
|---|---|---|---|
| `copyFrom_11_3.testCopyFrom` | 60 | attendu `testValue`, obtenu `null` | lecture de `getContextMap().get(String.class)`, les clés de la map sont des chaînes (`key.getName()`), pas des `Class` |
| `copyFrom_11_3.testCopyFromWithNullSource` | 74 | attendu `initialValue`, obtenu `null` | la valeur `initialValue` est utilisée comme clé, la clé réelle est `java.lang.String` |
| `equals_14_4.testEqualsSymmetric` | 55 | attendu `true`, obtenu `false` | chaque contexte reçoit un `mock(JsonConfig.class)` différent, un mock n'est égal qu'à lui-meme |
| `equals_14_4.testEqualsTransitive` | 64 | attendu `true`, obtenu `false` | meme cause |

#### 3.2.1 correction des tests `copyFrom_11_3`
les 2 tests échouaient à l'exécution (`testCopyFrom` ligne 60, `testCopyFromWithNullSource` ligne 74), le fichier compilait mais les tests ne passaient pas, donc les erreurs étaient logiques
| # | Test | Erreur du test généré | Correction | Justification |
|---|------|----------------------|------------|------------------------|
| 1 | les deux | `getContextMap().get(String.class)`, la clé est une `Class`, alors le résultat est `null` | `getContextMap().get(String.class.getName())` | `set` stocke sous `key.getName()`, `getContextMap()` est une `Map<String,Object>`. |
| 2 | `testCopyFrom` | Le meme mock `JsonConfig` servait à la source et à la cible. | Ajout de `mockJsonConfigTarget` pour la cible. | Un test de copie doit partir de deux objets distincts. |
| 3 | les deux | `assertEquals(new Object(), getResolvedConfig(...))` échoue toujours, `Object.equals` compare l'identité, et l'objet est neuf. | Deux champs `initialResolvedConfigSourceObject` et `initialResolvedConfigTargetObject`, avec `assertSame`. | `setResolvedConfig` stocke la référence reçue  |
| 4 | `testCopyFrom` | `assertNull` sur `initialKey` (jsonConfigs et resolved), le test supposait que `copyFrom` vide la cible. | Les assertions vérifient maintenant que la valeur de `initialKey` est **inchangée**. | `copyFrom` vérifie explicitement si c'est un `null` avant de remplacer |
| 5 | `testCopyFrom` | `assertNull(getContextMap().get("initialValue"))` n'a pas de sens, `"initialValue"` est une valeur, pas une clé. | Assertion supprimée. | Elle ne pouvait pas échouer mais ne détectait rien |


#### 3.2.2 correction des tests `equals_14_4`

Les tests `Symmetric` (ligne 55) et `Transitive` (ligne 64) échouaient. Le fichier compilait. La correction était de nature logique.

| # | Test | Erreur du test généré | Correction | Justification |
|---|------|----------------------|------------|---------------|
| 1 | `setUp`, `Symmetric`, `Transitive` | Chaque `ParseContext` recevait son propre `mock(JsonConfig.class)`. Un mock Mockito a une égalité par identité, donc `jsonConfigs.equals(...)` retournait `false`. | `setJsonConfig("testJsonConfig", "mockJsonConfig")` (surcharge `String`) pour les trois contextes. | Cette surcharge enveloppe la chaine dans le `record StringJsonConfig` . Un `record` a une égalité par valeur, donc deux contextes construits pareil sont égaux. |


### 3.3 critique des tests générés

| Fichier | Atouts | Faiblesses |
|---|---|---|
| `isEmpty_10_0` | 4 cas qui couvrent toutes les combinaisons de `context.isEmpty() && jsonConfigs.isEmpty()`, oracles précis. Les 4 mutants de `isEmpty` sont tués | Mock et `openMocks` inutiles. N'essaie pas le retour à l'état vide ni l'exclusion de `resolvedConfigs`. |
| `equals_14_4` | Couvre `null`, autre classe, contexte différent, config différente. | Mocks distincts → symétrie et transitivité fausses, mauvaise compréhension des arguments utilisés par les méthodes testées |
| `get_1_1` | Deux cas clairs (clé absente, clé présente). | Pas de test de suppression par `set(cle, null)`. |
| `get_2_1` | Les deux branches couvertes. | 7 tests mais des doublons, noms trompeurs, `@Mock` inutilisé. |
| `getJsonConfig_5_0` | Cas présent. | Pas de cas absent, remplacé ou supprimé, `@InjectMocks` superflu. |
| `hasJsonConfig_9_0` | `true` / `false`. | Pas de cas après suppression. |
| `getResolvedConfig_7_2` | Branche `resolvedConfigs == null` couverte par réflexion. | Le cas « clé associée à null » équivaut à « clé absente ». |
| `newMetadata_12_4` | Utilise factory configurée dans le contexte. | Oracle faible (`Metadata.class`), ne prouve pas que la limite est appliquée, pas de cas sans factory. |
| `toString_16_0` | Format exact vérifié. | Seulement le cas vide. |
| `copyFrom_11_3` | Plusieurs comportements dans un test. | Oracles faux (voir 3.2) avant correction, meme mock dans la source et la cible. |

### 3.4 Comparaison avec les tests originaux (`ParseContextTest.java`)
**Tests écrits à la main (`ParseContextTest`)**
les oracles sont précis, chaque test couvre un comportement spécifique et retourne aussi un message d'erreur explicatif en cas d'erreur, mais seule cette méthode est testée, ce qui donne une faible couverture et beaucoup de mutants survivants.
**Tests générés par IA**
Ils couvrent plus de méthodes et de branches, mais les oracles sont plus pauvres et certains tests sont incorrects voire inutiles. Des corrections étaient nécessaires pour que les tests passent et soient corrects. Les tests générés sont plus nombreux et couvrent plus de branches mais sont moins précis et moins robustes. 

## 4. Détection de mutants avec les tests générés

Tableau comparatif des résultats de mutation avec les tests originaux et après ajout des tests générés par ChatUniTest.

| | originaux | originaux + générés |
|---|---|---|
| Mutants tués | 12 | 38 |
| Survivants | 3 | 1 |
| Sans couverture | 28 | 4 |
| **Score de mutation** (tués / 43) | **27,9 %** | **88,4 %** |
| Force des tests (tués / (tués + survivants)) | 80,0 % | 97,4 % |
 
Avec les originaux seuls, presque tous les mutants atteints sont tués (12 sur 15), mais 28 mutants ne sont pas couverts donc le code n'est jamais exécuté. Le score faible vient donc du manque de couverture des tests, pas de leur qualité 
 
### 4.1 Les tests générés détectent-ils tous les mutants ?
 
**Non**, 5 mutants restent vivants sur 43.
 
### 4.2 Mutants détectés grâce aux tests générés : lesquels et pourquoi
 
Sur les 26 mutants tués par les tests générés, 25 étaient des mutants sans couverture avec les tests originaux. Le mutant restant était un survivant des originaux.
 
| Méthode (nb de mutants tués en plus) | Test tueur | Pourquoi il détecte la mutation |
|---|---|---|
| `equals` (9) | `testEqualsReflexive`, `testEqualsNull`, `testEqualsDifferentContext`, `testEqualsDifferentJsonConfig`, `testEqualsTransitive` | Chaque test teste une branche, `this == o` (réflexivité), `o == null` (retour `false`), `context` différent, `jsonConfigs` différents. Inverser une condition ou forcer un `return true/false` change au moins un de ces résultats. |
| `get` (4) | `testGetWithExistingKey`, `testGetWithEmptyKey`, `testGetWithEmptyDefaultValue` | Ils vérifient la valeur retournée si la clé existe, la valeur par défaut si elle n'existe pas. Un `return null` ou une condition inversée change l'un des deux. |
| `isEmpty` (4) | `testIsEmpty_WhenContextAndJsonConfigsAreEmpty`, `testIsEmpty_WhenContextIsNotEmpty` | L'oracle est `true` quand tout est vide et `false` sinon, ce qui distingue les retours constants et l'inversion du `&&`. |
| `hasJsonConfig` (2) | `testHasJsonConfigTrue`, `testHasJsonConfigFalse` | Un cas présent et un cas absent. Un retour toujours `true` ou toujours `false` échoue sur l'un des deux. |
| `getContextMap`, `getJsonConfig`, `newMetadata`, `set`, `toString` (1 chacun) | `testCopyFromWithNullSource`, `testGetJsonConfig`, `testNewMetadata`, `testGetWithExistingKey`, `testToString` |  Un test qui vérifie sa valeur de retour (non nulle, non vide, ou égale à la valeur stockée) suffit à tuer la mutation « retourne null / objet vide ». |
| `copyFrom` (2 survivants des originaux, ligne 241 et ligne 255) | `testCopyFrom` | Voir ci-dessous. |
 
**Les deux survivants des originaux tués par les générés.**
- **Ligne 241**  aucun test original n'appelle `set(...)`, donc la map typée n'est jamais remplie. `testCopyFrom` met une entrée typée dans la source et vérifie qu'elle arrive dans la cible.
- **Ligne 255** (condition `resolvedConfigs == null` inversée).  Dans les tests originaux, la boucle de la ligne 250 a déjà vidé la clé concernée, si bien qu'une nouvelle map donne le meme résultat. `testCopyFrom` vérifie qu'une clé **non écrasée** de la cible survit, ce qu'une map neuve effacerait .

### 4.3 Mutants non détectés par les tests générés
Fichier : `tika-core/src/test/java/org/apache/tika/parser/ParseContextManuelTest.java`. Il cible les 5 mutants de `ParseContext` qui survivent aux tests originaux et aux tests générés 
 
### `testHashCodeCombinesContextAndJsonConfigs`
 
- **Intention:** Vérifier que `hashCode()` combine les deux maps selon `31 * hash(context) + hash(jsonConfigs)` (lignes 321 à 323).
- **Mutants visés:** Ligne 322, deux remplacements d'opérateur arithmétique, et ligne 323 (`return result` remplacé par 0). Tous `NO_COVERAGE`, aucun test généré pour `hashCode` n'a été généré.
- **Données:** Un `ParseContext` avec une entrée typée (`String.class` → `"test"`) et une config JSON (`"test"` → `{"a":1}`). Les deux maps sont non vides, donc la ligne 322 est exécutée et la ligne 323 retourne un résultat non nul. Avec les mutations, le résultat serait soit `0`, soit une valeur incorrecte.
  
- **Oracle:** La valeur attendue est recalculée dans le test avec la formule du code, à partir de `getContextMap().hashCode()` et `getJsonConfigs().hashCode()`. Le test compare `hashCode()` à cette valeur. Un `assertNotEquals(0, …)` écarte le cas où le mutant « retourne 0 » passerait par coïncidence.

### `testSetJsonConfigNullRemovesResolvedConfig`
 
- **Intention:** Vérifier que retirer une config JSON (valeur `null`) retire aussi la config résolue du meme nom.
- **Mutant visé:** Ligne 126, condition `resolvedConfigs != null` inversée (`NO_COVERAGE`). Le test exécute aussi les lignes 125 à 127, que JaCoCo signalait non couvertes.

- **Données:** Config JSON et config résolue enregistrées sous le **meme nom** (`"test"`), puis `setJsonConfig("test", (JsonConfig) null)`. Le cast lève l'ambiguïté avec la surcharge `(String, String)`. La config résolue est non `null`, donc la branche de la ligne 126 est prise. Avec la condition inversée, elle serait sautée et la config résolue resterait.

- **Oracle:** Le code (lignes 124 à 128) retire la config JSON puis la config résolue. Après l'appel, `getJsonConfig` retourne `null`, `hasJsonConfig` retourne `false`, `getResolvedConfig` retourne `null`.


### `testSetResolvedConfigKeepsPreviousEntries`
 
- **Intention:** Vérifier que `setResolvedConfig` ajoute une entrée à la map existante sans effacer les précédentes.
- **Mutant visé:** Ligne 202, condition `resolvedConfigs == null` inversée (`SURVIVED`). Avec la mutation, la map est recréée à chaque appel et seule la dernière entrée subsiste.

- **Données:** Deux appels successifs, avec deux noms différents (`"a"`, `"b"`) et deux objets distincts créés par `new Object()`. Des objets distincts permettent à `assertSame` de distinguer les deux entrées.

- **Oracle:** Le code (lignes 201 à 210) ne fait que `put` dans la map existante. Après les deux appels, `getResolvedConfig("a")` doit retourner le premier objet et `getResolvedConfig("b")` le second, par identité.

 
### Résultat mesuré 
 
| | Tests originaux | + tests générés (B2) | + tests manuels |
|---|---|---|---|
| Mutants tués | 12 / 43 | 38 / 43 | **43 / 43** |
| Score de mutation | 27,9 % | 88,4 % | **100 %** |
| Mutants vivants | 3 survivants, 28 sans couverture | 1 survivant, 4 sans couverture | **0** |
 
Les 5 mutants ciblés sont tous tués (le fichier PIT n'indique qu'un seul test tueur par mutant) :
 
| Mutant | Test tueur |
|---|---|
| `hashCode` ligne 322 (×2) | `testHashCodeCombinesContextAndJsonConfigs` |
| `hashCode` ligne 323 | `testHashCodeCombinesContextAndJsonConfigs` |
| `setJsonConfig` ligne 126 | `testSetJsonConfigNullRemovesResolvedConfig` |
| `setResolvedConfig` ligne 202 | `testSetResolvedConfigKeepsPreviousEntries` |
 

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