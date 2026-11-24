package moveMethodRefactoring.similarity;

import moveMethodRefactoring.model.ClassInfo;
import moveMethodRefactoring.model.MethodInfo;
import moveMethodRefactoring.text.TermBag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The document collection D used for idf (Equation 4.3).
 *
 * The thesis does not say what the collection is. It is taken here as every
 * method document plus every class document of the analysed system. (If it
 * were only m and C, N = 2 and every shared term would get idf = 0, so the
 * cosine would always be 0.)
 */
public final class Corpus {

    private final Map<String, Integer> documentFrequency = new HashMap<>();
    private int documentCount;

    public static Corpus of(Collection<ClassInfo> classes) {
        Corpus corpus = new Corpus();
        for (ClassInfo c : classes) {
            corpus.addDocument(c.document());
            for (MethodInfo m : c.getMethods()) {
                corpus.addDocument(m.getBodyTerms());
            }
        }
        return corpus;
    }

    public void addDocument(TermBag doc) {
        documentCount++;
        for (String term : doc.terms()) {
            Integer old = documentFrequency.get(term);
            documentFrequency.put(term, old == null ? 1 : old + 1);
        }
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public int documentFrequency(String term) {
        Integer df = documentFrequency.get(term);
        return df == null ? 0 : df;
    }

    /** Equation 4.3: idf(t) = log2(N / |{d in D : t in d}|). */
    public double idf(String term) {
        int df = documentFrequency(term);
        if (df == 0 || documentCount == 0) return 0.0;   // term unknown to the corpus
        return log2((double) documentCount / df);
    }

    static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
