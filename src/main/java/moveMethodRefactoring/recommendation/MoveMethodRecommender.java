package moveMethodRefactoring.recommendation;

import moveMethodRefactoring.model.ClassInfo;
import moveMethodRefactoring.model.MethodInfo;
import moveMethodRefactoring.model.Recommendation;
import moveMethodRefactoring.model.Recommendation.Candidate;
import moveMethodRefactoring.similarity.ContextualSimilarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Algorithm 1 - Recommendation of Move Method Refactorings (modified):
 *
 * <pre>
 * for each method m in S
 *     C <- getClass(m)
 *     T <- null
 *     for each class Ci in S
 *         if contextSim(m, Ci) > contextSim(m, C) then T <- T + Ci
 *     C' <- bestClass(m, T)
 * </pre>
 *
 * Interfaces are not accepted as target classes, and a method's own class is
 * never a candidate.
 */
public final class MoveMethodRecommender {

    private final ContextualSimilarity similarity;
    private final double minimumMargin;
    private final int explainTerms;

    /** @param minimumMargin how much higher contextSim(m, Ci) must be than contextSim(m, C); 0 = Algorithm 1 as written. */
    public MoveMethodRecommender(ContextualSimilarity similarity, double minimumMargin, int explainTerms) {
        if (minimumMargin < 0) throw new IllegalArgumentException("minimumMargin must be >= 0");
        this.similarity = similarity;
        this.minimumMargin = minimumMargin;
        this.explainTerms = explainTerms;
    }

    public List<Recommendation> recommend(Collection<ClassInfo> system) {
        List<Recommendation> result = new ArrayList<>();
        for (ClassInfo c : system) {
            for (MethodInfo m : c.getMethods()) {
                Recommendation r = recommend(m, system);
                if (r != null) result.add(r);
            }
        }
        return result;
    }

    /** Returns the recommendation for one method, or null when T is empty. */
    public Recommendation recommend(MethodInfo m, Collection<ClassInfo> system) {
        if (m.getBodyTerms().isEmpty()) return null;            // nothing to compare

        ClassInfo current = m.getOwner();                         // Line 2
        double currentSim = similarity.contextSim(m, current);
        List<Candidate> t = new ArrayList<>();                    // Line 3

        for (ClassInfo ci : system) {                             // Line 4
            if (ci == current || ci.isInterface()) continue;
            double sim = similarity.contextSim(m, ci);
            if (sim > currentSim + minimumMargin) {               // Line 5 (modified)
                t.add(new Candidate(ci, sim));                    // Line 6
            }
        }
        if (t.isEmpty()) return null;

        List<Candidate> sorted = bestClassFirst(t);                   // Line 9
        Candidate best = sorted.get(0);
        best.setExplanation(similarity.explain(m, best.getClazz(), explainTerms));
        return new Recommendation(m, currentSim, similarity.explain(m, current, explainTerms), sorted);
    }

    /** bestClass(m, T): candidates sorted by contextual similarity, highest first (ties by name). */
    static List<Candidate> bestClassFirst(List<Candidate> t) {
        List<Candidate> sorted = new ArrayList<>(t);
        Collections.sort(sorted, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate a, Candidate b) {
                int bySim = Double.compare(b.getSimilarity(), a.getSimilarity());
                return bySim != 0 ? bySim
                        : a.getClazz().getQualifiedName().compareTo(b.getClazz().getQualifiedName());
            }
        });
        return sorted;
    }
}
