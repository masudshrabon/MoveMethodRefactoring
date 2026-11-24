package moveMethodRefactoring.model;

import moveMethodRefactoring.text.TermBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class (or interface) of the analysed system together with the terms of
 * its declared fields and declared methods (Algorithm 3, Line 2).
 */
public final class ClassInfo {

    private final String qualifiedName;
    private final boolean isInterface;
    private final TermBag fieldTerms = new TermBag();
    private final List<MethodInfo> methods = new ArrayList<>();

    public ClassInfo(String qualifiedName, boolean isInterface) {
        this.qualifiedName = qualifiedName;
        this.isInterface = isInterface;
    }

    public String getQualifiedName() { return qualifiedName; }
    public boolean isInterface()     { return isInterface; }
    public TermBag getFieldTerms()   { return fieldTerms; }
    public List<MethodInfo> getMethods() { return Collections.unmodifiableList(methods); }

    public void addMethod(MethodInfo method) {
        methods.add(method);
    }

    /** The class document: terms of all declared fields and declared method names. */
    public TermBag document() {
        TermBag doc = fieldTerms.copy();
        for (MethodInfo m : methods) {
            doc.addAll(m.getNameTerms());
        }
        return doc;
    }

    /**
     * The class document without the given method's own name. Used when the
     * class is the method's current class, so that the method is not counted
     * as evidence for staying where it is.
     */
    public TermBag documentExcluding(MethodInfo method) {
        TermBag doc = document();
        if (method.getOwner() == this) {
            doc.subtract(method.getNameTerms());
        }
        return doc;
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
