package moveMethodRefactoring.similarity;

import moveMethodRefactoring.text.TermBag;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds the tf-idf vector of a document (Algorithm 3, Lines 3-5).
 *
 * tf     = 1 + log2(raw count)       (logarithmic normalisation, Section 4.4 text)
 * idf    = log2(N / df)              (Equation 4.3)
 * tf-idf = tf * idf                  (Equation 4.4)
 */
public final class TfIdfVectorizer {

    private final Corpus corpus;

    public TfIdfVectorizer(Corpus corpus) {
        this.corpus = corpus;
    }

    public Map<String, Double> vectorize(TermBag doc) {
        Map<String, Double> vector = new HashMap<>();
        for (String term : doc.terms()) {
            double weight = normalizedTf(doc.frequency(term)) * corpus.idf(term);
            if (weight != 0.0) {
                vector.put(term, weight);
            }
        }
        return vector;
    }

    static double normalizedTf(int rawCount) {
        return rawCount <= 0 ? 0.0 : 1.0 + Corpus.log2(rawCount);
    }
}
