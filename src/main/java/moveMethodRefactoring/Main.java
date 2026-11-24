package moveMethodRefactoring;

import moveMethodRefactoring.model.Recommendation;
import moveMethodRefactoring.report.CsvReportWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point.
 *
 * Usage: java moveMethodRefactoring.Main [projectRoot] [outputCsv] [options]
 *   projectRoot  folder with the Java sources to analyse (default: dataset/SampleShop)
 *   outputCsv    result file (default: move_method_suggestions.csv)
 * Options (all off by default = Algorithm 1 / Algorithm 3 as written):
 *   --margin=0.1        contextSim(m,Ci) must exceed contextSim(m,C) by this much
 *   --nested            also analyse nested/inner classes
 *   --exclude-own-name  leave m's own name out of its current class's document
 *   --locals            add parameter/local variable names to method documents
 */
public final class Main {

    /** The sample input project shipped with this repository (relative to the project folder). */
    private static final String DEFAULT_PROJECT = "dataset/SampleShop";
    private static final String DEFAULT_OUTPUT = "move_method_suggestions.csv";

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        SuggesterOptions options = new SuggesterOptions();
        List<String> positional = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("--margin=")) options.minimumMargin = Double.parseDouble(arg.substring(9));
            else if (arg.equals("--nested")) options.includeNestedClasses = true;
            else if (arg.equals("--exclude-own-name")) options.excludeOwnNameFromCurrentClass = true;
            else if (arg.equals("--locals")) options.includeLocalVariables = true;
            else if (arg.startsWith("--")) {
                System.err.println("Unknown option: " + arg);
                System.exit(2);
            } else positional.add(arg);
        }

        Path projectRoot = Paths.get(positional.size() > 0 ? positional.get(0) : DEFAULT_PROJECT);
        Path output = Paths.get(positional.size() > 1 ? positional.get(1) : DEFAULT_OUTPUT);

        if (!Files.isDirectory(projectRoot)) {
            System.err.println("Project folder not found: " + projectRoot);
            System.exit(1);
        }

        MoveMethodSuggester suggester = new MoveMethodSuggester(options);
        List<Recommendation> recommendations = suggester.suggest(projectRoot);
        new CsvReportWriter().write(recommendations, output);

        System.out.println("Options: " + options);
        System.out.printf("Analysed %d classes, %d methods.%n",
                suggester.getClassCount(), suggester.getMethodCount());
        for (String skipped : suggester.getSkippedFiles()) {
            System.out.println("Skipped: " + skipped);
        }
        System.out.printf("%d Move Method suggestion(s) saved to %s%n",
                recommendations.size(), output.toAbsolutePath());
    }
}
