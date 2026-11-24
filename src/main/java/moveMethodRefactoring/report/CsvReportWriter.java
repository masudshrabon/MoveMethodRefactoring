package moveMethodRefactoring.report;

import moveMethodRefactoring.model.Recommendation;
import moveMethodRefactoring.model.Recommendation.Candidate;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/** Writes one CSV row per Move Method suggestion: the method and its recommended target class. */
public final class CsvReportWriter {

    private static final String HEADER =
            "Source Class,Method,Current Class Similarity,Recommended Class,"
            + "Recommended Class Similarity,Candidate Count,"
            + "Shared Terms (Current),Shared Terms (Recommended),All Candidates";

    public void write(List<Recommendation> recommendations, Path output) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            out.println(HEADER);
            for (Recommendation r : recommendations) {
                Candidate best = r.getBest();
                out.println(String.join(",",
                        csv(r.getSourceClass().getQualifiedName()),
                        csv(r.getMethod().getSignature()),
                        number(r.getCurrentSimilarity()),
                        csv(best.getClazz().getQualifiedName()),
                        number(best.getSimilarity()),
                        String.valueOf(r.getCandidates().size()),
                        csv(r.getCurrentExplanation()),
                        csv(best.getExplanation()),
                        csv(allCandidates(r))));
            }
        }
    }

    private static String allCandidates(Recommendation r) {
        StringBuilder sb = new StringBuilder();
        for (Candidate c : r.getCandidates()) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(c.getClazz().getQualifiedName()).append('=').append(number(c.getSimilarity()));
        }
        return sb.toString();
    }

    private static String number(double d) {
        return String.format(Locale.ROOT, "%.4f", d);
    }

    /** Quotes a value when needed (signatures contain commas). */
    static String csv(String value) {
        if (value.indexOf(',') < 0 && value.indexOf(';') < 0 && value.indexOf('"') < 0 && value.indexOf('\n') < 0) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
