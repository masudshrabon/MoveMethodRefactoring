package moveMethodRefactoring.parsing;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.Set;

/**
 * Collects the identifiers that make up a method's context
 * (Algorithm 3, Line 1): invoked methods and used fields - only those
 * declared in the analysed project (see {@link ProjectVocabulary}).
 *
 * - JDK / library calls and fields are skipped.
 * - Class names used as qualifiers (AppLogger in AppLogger.error()) are not
 *   fields, so they are skipped.
 * - Parameters and local variables are skipped unless includeLocals is set;
 *   then their names count too, since they are also written by the developer.
 *
 * Every occurrence is collected, so term frequencies are preserved.
 */
final class MethodBodyTermCollector extends VoidVisitorAdapter<List<String>> {

    private final ProjectVocabulary vocabulary;
    private final Set<String> localNames;
    private final boolean includeLocals;

    MethodBodyTermCollector(ProjectVocabulary vocabulary, Set<String> localNames, boolean includeLocals) {
        this.vocabulary = vocabulary;
        this.localNames = localNames;
        this.includeLocals = includeLocals;
    }

    @Override
    public void visit(MethodCallExpr n, List<String> identifiers) {
        if (vocabulary.isProjectMethod(n.getNameAsString())) {
            identifiers.add(n.getNameAsString());
        }
        super.visit(n, identifiers);
    }

    @Override
    public void visit(MethodReferenceExpr n, List<String> identifiers) {
        if (vocabulary.isProjectMethod(n.getIdentifier())) {
            identifiers.add(n.getIdentifier());
        }
        super.visit(n, identifiers);
    }

    @Override
    public void visit(FieldAccessExpr n, List<String> identifiers) {
        if (vocabulary.isProjectField(n.getNameAsString())) {
            identifiers.add(n.getNameAsString());
        }
        super.visit(n, identifiers);
    }

    @Override
    public void visit(NameExpr n, List<String> identifiers) {
        String name = n.getNameAsString();
        if (localNames.contains(name)) {
            if (includeLocals) identifiers.add(name);      // a local shadows a field of the same name
        } else if (vocabulary.isProjectField(name)) {
            identifiers.add(name);                         // class names / library names are not project fields
        }
        super.visit(n, identifiers);
    }

    @Override
    public void visit(VariableDeclarator n, List<String> identifiers) {
        if (includeLocals) {
            identifiers.add(n.getNameAsString());
        }
        super.visit(n, identifiers);
    }
}
