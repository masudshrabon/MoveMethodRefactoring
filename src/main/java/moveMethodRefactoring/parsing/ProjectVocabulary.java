package moveMethodRefactoring.parsing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The identifiers written by the project's developers: every field (and enum
 * constant) and every method declared anywhere in the analysed sources.
 *
 * A method body identifier is part of the context only if it is in this
 * vocabulary, so calls and fields of the JDK or third-party libraries
 * ({@code println}, {@code equals}, {@code size}, {@code out}) are ignored.
 *
 * Matching is by name (no type resolution): a library call is still counted
 * if the project itself declares a method with the same name, e.g. when it
 * overrides {@code toString()}.
 */
final class ProjectVocabulary {

    private final Set<String> fieldNames = new HashSet<>();
    private final Set<String> methodNames = new HashSet<>();

    static ProjectVocabulary of(Collection<CompilationUnit> units) {
        ProjectVocabulary v = new ProjectVocabulary();
        for (CompilationUnit cu : units) {
            for (FieldDeclaration f : cu.findAll(FieldDeclaration.class)) {
                for (VariableDeclarator var : f.getVariables()) {
                    v.fieldNames.add(var.getNameAsString());
                }
            }
            for (EnumConstantDeclaration e : cu.findAll(EnumConstantDeclaration.class)) {
                v.fieldNames.add(e.getNameAsString());
            }
            for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
                v.methodNames.add(m.getNameAsString());
            }
        }
        return v;
    }

    boolean isProjectField(String name) {
        return fieldNames.contains(name);
    }

    boolean isProjectMethod(String name) {
        return methodNames.contains(name);
    }
}
