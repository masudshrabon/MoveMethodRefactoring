package moveMethodRefactoring.similarity;

import moveMethodRefactoring.model.ClassInfo;
import moveMethodRefactoring.model.MethodInfo;
import moveMethodRefactoring.text.TermBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Algorithm 3 - Contextual Similarity Function: contextSim(m, C).
 * Cosine similarity between the tf-idf vector of method m and that of class C.
 *
 * Class vectors are computed once and cached.
 */
public final class ContextualSimilarity {

    private final TfIdfVectorizer vectorizer;
    private final boolean excludeOwnName;
    private final Map<ClassInfo, Map<String, Double>> classVectorCache = new HashMap<>();
    private final Map<MethodInfo, Map<String, Double>> methodVectorCache = new HashMap<>();

    /**
     * @param excludeOwnName leave m's own name out of its current class's document
     *                       (false = Algorithm 3 as written)
     */
    public ContextualSimilarity(Corpus corpus, boolean excludeOwnName) {
        this.vectorizer = new TfIdfVectorizer(corpus);
        this.excludeOwnName = excludeOwnName;
    }

    public double contextSim(MethodInfo m, ClassInfo c) {
        return CosineSimilarity.between(methodVector(m), classVector(m, c));
    }

    /**
     * The terms that contribute most to contextSim(m, C), as "term=share" where
     * the shares add up to the similarity score. Used to check why a score is high.
     */
    public String explain(MethodInfo m, ClassInfo c, int topN) {
        Map<String, Double> mv = methodVector(m);
        Map<String, Double> cv = classVector(m, c);
        double norms = CosineSimilarity.magnitude(mv) * CosineSimilarity.magnitude(cv);
        if (norms == 0.0) return "";

        List<Map.Entry<String, Double>> shares = new ArrayList<>();
        for (Map.Entry<String, Double> e : mv.entrySet()) {
            Double other = cv.get(e.getKey());
            if (other != null) {
                shares.add(new java.util.AbstractMap.SimpleEntry<>(e.getKey(), e.getValue() * other / norms));
            }
        }
        Collections.sort(shares, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
                return Double.compare(b.getValue(), a.getValue());
            }
        });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shares.size() && i < topN; i++) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(shares.get(i).getKey()).append('=')
              .append(String.format(Locale.ROOT, "%.3f", shares.get(i).getValue()));
        }
        return sb.toString();
    }

    private Map<String, Double> methodVector(MethodInfo m) {
        Map<String, Double> v = methodVectorCache.get(m);
        if (v == null) {
            v = vectorizer.vectorize(m.getBodyTerms());
            methodVectorCache.put(m, v);
        }
        return v;
    }

    private Map<String, Double> classVector(MethodInfo m, ClassInfo c) {
        if (excludeOwnName && m.getOwner() == c) {
            TermBag doc = c.documentExcluding(m);
            return vectorizer.vectorize(doc);
        }
        Map<String, Double> v = classVectorCache.get(c);
        if (v == null) {
            v = vectorizer.vectorize(c.document());
            classVectorCache.put(c, v);
        }
        return v;
    }
}
