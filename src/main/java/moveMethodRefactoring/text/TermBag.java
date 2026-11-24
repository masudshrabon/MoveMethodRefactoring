package moveMethodRefactoring.text;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A "document" in the Bag-of-Words model: each term with its number of
 * occurrences (raw term frequency). Word order is not kept.
 */
public final class TermBag {

    private final Map<String, Integer> counts = new HashMap<>();

    public void add(String term) {
        add(term, 1);
    }

    public void add(String term, int times) {
        if (times <= 0) return;
        Integer old = counts.get(term);
        counts.put(term, old == null ? times : old + times);
    }

    public void addAll(TermBag other) {
        for (Map.Entry<String, Integer> e : other.counts.entrySet()) {
            add(e.getKey(), e.getValue());
        }
    }

    /** Removes the occurrences contained in {@code other} (never below zero). */
    public void subtract(TermBag other) {
        for (Map.Entry<String, Integer> e : other.counts.entrySet()) {
            Integer old = counts.get(e.getKey());
            if (old == null) continue;
            int left = old - e.getValue();
            if (left > 0) counts.put(e.getKey(), left);
            else counts.remove(e.getKey());
        }
    }

    public TermBag copy() {
        TermBag c = new TermBag();
        c.counts.putAll(counts);
        return c;
    }

    public int frequency(String term) {
        Integer c = counts.get(term);
        return c == null ? 0 : c;
    }

    public Set<String> terms() {
        return Collections.unmodifiableSet(counts.keySet());
    }

    public boolean isEmpty() {
        return counts.isEmpty();
    }

    @Override
    public String toString() {
        return counts.toString();
    }
}
