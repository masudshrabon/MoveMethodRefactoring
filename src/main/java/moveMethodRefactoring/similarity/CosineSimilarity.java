package moveMethodRefactoring.similarity;

import java.util.Map;

/** Equation 4.5: cosine of the angle between two sparse vectors. */
public final class CosineSimilarity {

    private CosineSimilarity() {
    }

    public static double between(Map<String, Double> a, Map<String, Double> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        Map<String, Double> small = a.size() <= b.size() ? a : b;
        Map<String, Double> large = small == a ? b : a;

        double dot = 0.0;
        for (Map.Entry<String, Double> e : small.entrySet()) {
            Double other = large.get(e.getKey());
            if (other != null) dot += e.getValue() * other;
        }
        double norms = magnitude(a) * magnitude(b);
        return norms == 0.0 ? 0.0 : dot / norms;
    }

    static double magnitude(Map<String, Double> v) {
        double sum = 0.0;
        for (double x : v.values()) sum += x * x;
        return Math.sqrt(sum);
    }
}
