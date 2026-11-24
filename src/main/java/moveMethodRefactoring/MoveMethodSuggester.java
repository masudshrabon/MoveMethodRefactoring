package moveMethodRefactoring;

import moveMethodRefactoring.model.ClassInfo;
import moveMethodRefactoring.model.Recommendation;
import moveMethodRefactoring.parsing.ProjectParser;
import moveMethodRefactoring.recommendation.MoveMethodRecommender;
import moveMethodRefactoring.similarity.ContextualSimilarity;
import moveMethodRefactoring.similarity.Corpus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Suggests Move Method refactorings for a Java project:
 * parse -> build corpus -> contextual similarity (Algorithm 3)
 * -> move method recommendation (Algorithm 1, contextSim only).
 */
public final class MoveMethodSuggester {

    private final SuggesterOptions options;
    private List<String> skippedFiles;
    private int classCount;
    private int methodCount;

    public MoveMethodSuggester(SuggesterOptions options) {
        this.options = options;
    }

    public List<Recommendation> suggest(Path projectRoot) throws IOException {
        ProjectParser parser = new ProjectParser(options);
        List<ClassInfo> classes = parser.parse(projectRoot);
        skippedFiles = parser.getSkippedFiles();
        return suggest(classes);
    }

    /** Runs the algorithm on an already-built model (used by tests). */
    public List<Recommendation> suggest(List<ClassInfo> classes) {
        classCount = classes.size();
        methodCount = 0;
        for (ClassInfo c : classes) methodCount += c.getMethods().size();

        Corpus corpus = Corpus.of(classes);
        ContextualSimilarity similarity =
                new ContextualSimilarity(corpus, options.excludeOwnNameFromCurrentClass);
        return new MoveMethodRecommender(similarity, options.minimumMargin, options.explainTerms)
                .recommend(classes);
    }

    public List<String> getSkippedFiles() { return skippedFiles; }
    public int getClassCount()            { return classCount; }
    public int getMethodCount()           { return methodCount; }
}
