package moveMethodRefactoring.model;

import java.util.Collections;
import java.util.List;

/**
 * Result of Algorithm 1 for one method: a Move Method refactoring suggestion -
 * candidate and should be moved to {@code targetClass} (the bestClass of T).
 */
public final class Recommendation {

    /** A class from the candidate list T with its contextual similarity. */
    public static final class Candidate {
        private final ClassInfo clazz;
        private final double similarity;
        private String explanation = "";

        public Candidate(ClassInfo clazz, double similarity) {
            this.clazz = clazz;
            this.similarity = similarity;
        }

        /** Top shared terms behind the score, e.g. "name=0.21; get=0.05". */
        public String getExplanation()             { return explanation; }
        public void setExplanation(String text)    { this.explanation = text; }

        public ClassInfo getClazz()     { return clazz; }
        public double getSimilarity()   { return similarity; }
    }

    private final MethodInfo method;
    private final double currentSimilarity;
    private final List<Candidate> candidates;   // T, sorted best first
    private final String currentExplanation;

    public Recommendation(MethodInfo method, double currentSimilarity, String currentExplanation,
                          List<Candidate> candidates) {
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("A recommendation needs at least one candidate class");
        }
        this.method = method;
        this.currentSimilarity = currentSimilarity;
        this.currentExplanation = currentExplanation;
        this.candidates = Collections.unmodifiableList(candidates);
    }

    public MethodInfo getMethod()          { return method; }
    public ClassInfo getSourceClass()      { return method.getOwner(); }
    public double getCurrentSimilarity()   { return currentSimilarity; }
    public List<Candidate> getCandidates() { return candidates; }
    public String getCurrentExplanation()  { return currentExplanation; }

    /** C' = bestClass(m, T). */
    public Candidate getBest()             { return candidates.get(0); }
}
