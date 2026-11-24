package moveMethodRefactoring.model;

import moveMethodRefactoring.text.TermBag;

/**
 * A method of the analysed system.
 *
 * bodyTerms - the method's document for contextual similarity
 *             (Algorithm 3, Line 1: used fields, invoked methods and
 *             declared local variables, tokenized and stemmed).
 * nameTerms - the terms of the method's own name; they are part of the
 *             owning class's document (Algorithm 3, Line 2).
 */
public final class MethodInfo {

    private final ClassInfo owner;
    private final String name;
    private final String signature;
    private final TermBag bodyTerms;
    private final TermBag nameTerms;

    public MethodInfo(ClassInfo owner, String name, String signature,
                      TermBag bodyTerms, TermBag nameTerms) {
        this.owner = owner;
        this.name = name;
        this.signature = signature;
        this.bodyTerms = bodyTerms;
        this.nameTerms = nameTerms;
    }

    public ClassInfo getOwner()     { return owner; }
    public String getName()         { return name; }
    /** Name plus parameter types, e.g. {@code print(String, int)}; unique within a class. */
    public String getSignature()    { return signature; }
    public TermBag getBodyTerms()   { return bodyTerms; }
    public TermBag getNameTerms()   { return nameTerms; }

    @Override
    public String toString() {
        return owner.getQualifiedName() + "#" + signature;
    }
}
