# MoveMethodRefactoring

Suggests **Move Method refactorings** for Java projects using contextual
(semantic) similarity between a method and the classes of the system.

For each method *m* with current class *C*, every other class *Ci* whose
contextual similarity to *m* is higher than that of *C* becomes a candidate;
the candidate with the highest similarity is suggested as the target class.

```
if contextSim(m, Ci) > contextSim(m, C) then T <- T + Ci
C' <- bestClass(m, T)
```

`contextSim` is the cosine similarity of tf-idf vectors built from
developer-written identifiers (field and method names), split on
camelCase/PascalCase, with Java keywords removed and Porter stemming applied.

## Structure (`src/main/java/moveMethodRefactoring`)

| Package | Classes |
|---|---|
| (root) | `Main`, `MoveMethodSuggester`, `SuggesterOptions` |
| `model` | `ClassInfo`, `MethodInfo`, `Recommendation` |
| `parsing` | `ProjectParser`, `ProjectVocabulary`, `MethodBodyTermCollector` |
| `text` | `IdentifierTokenizer`, `PorterStemmer`, `TermBag` |
| `similarity` | `Corpus`, `TfIdfVectorizer`, `CosineSimilarity`, `ContextualSimilarity` |
| `recommendation` | `MoveMethodRecommender` |
| `report` | `CsvReportWriter` |

## Requirements

Java 8 or newer, Maven (dependency: JavaParser 3.25.4).

## Run

```
java moveMethodRefactoring.Main <projectRoot> [output.csv] [options]
```

Options (all off by default):

| Option | Effect |
|---|---|
| `--margin=0.1` | a candidate must beat the current class by this margin |
| `--nested` | also analyse nested/inner classes |
| `--exclude-own-name` | leave the method's own name out of its current class's document |
| `--locals` | add parameter and local variable names to method documents |

Output: one CSV row per suggested move, with the current and target class
similarity and the shared terms behind each score.

## Sample dataset

`dataset/SampleShop` is a small project with three planted misplaced methods
and the expected output (`expected_results.csv`). Try:

```
java moveMethodRefactoring.Main dataset/SampleShop dataset/SampleShop/actual_results.csv
```
