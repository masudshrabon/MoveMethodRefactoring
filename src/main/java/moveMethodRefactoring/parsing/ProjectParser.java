package moveMethodRefactoring.parsing;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import moveMethodRefactoring.SuggesterOptions;
import moveMethodRefactoring.model.ClassInfo;
import moveMethodRefactoring.model.MethodInfo;
import moveMethodRefactoring.text.IdentifierTokenizer;
import moveMethodRefactoring.text.TermBag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses every .java file under a root folder and builds the class/method
 * model with tokenized, stemmed term bags.
 *
 * Only identifiers written by the project's developers are used: declared
 * field and method names for class documents, and - for method documents -
 * calls and field uses that refer to names declared in the project
 * (see {@link ProjectVocabulary}). Java keywords never appear as terms.
 *
 * Classes are keyed by fully-qualified name (so equal simple names in
 * different packages do not overwrite each other) and overloaded methods are
 * kept apart by signature. Only top-level classes are analysed unless
 * {@link SuggesterOptions#includeNestedClasses} is set.
 * Files that fail to parse are reported and skipped.
 */
public final class ProjectParser {

    private final JavaParser parser;
    private final SuggesterOptions options;
    private final IdentifierTokenizer tokenizer = new IdentifierTokenizer();
    private final List<String> skippedFiles = new ArrayList<>();
    private ProjectVocabulary vocabulary;

    public ProjectParser(SuggesterOptions options) {
        this.options = options;
        ParserConfiguration config = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE)
                .setCharacterEncoding(StandardCharsets.UTF_8);
        this.parser = new JavaParser(config);
    }

    public List<ClassInfo> parse(Path root) throws IOException {
        // Pass 1: parse every file and collect the names the developers declared.
        Map<Path, CompilationUnit> units = new LinkedHashMap<>();
        for (Path file : javaFiles(root)) {
            ParseResult<CompilationUnit> result = parser.parse(file);
            Optional<CompilationUnit> cu = result.getResult();
            if (!result.isSuccessful() || !cu.isPresent()) {
                skippedFiles.add(file + " : " + result.getProblems());
                continue;
            }
            units.put(file, cu.get());
        }
        vocabulary = ProjectVocabulary.of(units.values());

        // Pass 2: build class and method documents from project identifiers only.
        Map<String, ClassInfo> classes = new LinkedHashMap<>();
        for (Map.Entry<Path, CompilationUnit> unit : units.entrySet()) {
            for (ClassOrInterfaceDeclaration type : classesOf(unit.getValue())) {
                ClassInfo info = toClassInfo(type);
                if (classes.containsKey(info.getQualifiedName())) {
                    skippedFiles.add(unit.getKey() + " : duplicate class " + info.getQualifiedName() + " ignored");
                    continue;
                }
                classes.put(info.getQualifiedName(), info);
            }
        }
        return new ArrayList<>(classes.values());
    }

    /** Files that could not be parsed, with the reason. */
    public List<String> getSkippedFiles() {
        return skippedFiles;
    }

    private List<ClassOrInterfaceDeclaration> classesOf(CompilationUnit cu) {
        if (options.includeNestedClasses) {
            return cu.findAll(ClassOrInterfaceDeclaration.class);
        }
        List<ClassOrInterfaceDeclaration> topLevel = new ArrayList<>();
        for (TypeDeclaration<?> type : cu.getTypes()) {
            if (type instanceof ClassOrInterfaceDeclaration) {
                topLevel.add((ClassOrInterfaceDeclaration) type);
            }
        }
        return topLevel;
    }

    private static List<Path> javaFiles(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
                        .sorted()
                        .collect(Collectors.toList());
        }
    }

    private ClassInfo toClassInfo(ClassOrInterfaceDeclaration type) {
        String name = type.getFullyQualifiedName().orElse(type.getNameAsString());
        ClassInfo info = new ClassInfo(name, type.isInterface());

        for (FieldDeclaration field : type.getFields()) {
            for (VariableDeclarator var : field.getVariables()) {
                addTerms(info.getFieldTerms(), var.getNameAsString());
            }
        }

        for (MethodDeclaration md : type.getMethods()) {
            TermBag nameTerms = new TermBag();
            addTerms(nameTerms, md.getNameAsString());

            TermBag bodyTerms = new TermBag();
            Optional<BlockStmt> body = md.getBody();
            if (body.isPresent()) {
                List<String> identifiers = new ArrayList<>();
                MethodBodyTermCollector collector =
                        new MethodBodyTermCollector(vocabulary, localNames(md, body.get()),
                                options.includeLocalVariables);
                body.get().accept(collector, identifiers);
                for (String id : identifiers) addTerms(bodyTerms, id);
            }

            info.addMethod(new MethodInfo(info, md.getNameAsString(),
                    md.getSignature().asString(), bodyTerms, nameTerms));
        }
        return info;
    }

    /** Parameters, local variables, lambda and catch parameters of a method. */
    private static Set<String> localNames(MethodDeclaration md, BlockStmt body) {
        Set<String> names = new HashSet<>();
        for (Parameter p : md.getParameters()) names.add(p.getNameAsString());
        for (Parameter p : body.findAll(Parameter.class)) names.add(p.getNameAsString());
        for (VariableDeclarator v : body.findAll(VariableDeclarator.class)) names.add(v.getNameAsString());
        return names;
    }

    private void addTerms(TermBag bag, String identifier) {
        for (String term : tokenizer.tokenize(identifier)) {
            bag.add(term);
        }
    }
}
