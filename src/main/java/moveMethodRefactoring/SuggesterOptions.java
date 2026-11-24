package moveMethodRefactoring;

/**
 * Switches for the points that the thesis leaves open.
 * The defaults follow Algorithm 1 / Algorithm 3 as written.
 */
public final class SuggesterOptions {

    /** Extra margin in Line 5: contextSim(m, Ci) > contextSim(m, C) + margin. Default 0 (Algorithm 1). */
    public double minimumMargin = 0.0;

    /** Also analyse nested/inner classes. Default false: top-level classes only (as the original tool). */
    public boolean includeNestedClasses = false;

    /**
     * Leave m's own name out of its current class's document. Default false:
     * Algorithm 3, Line 2 indexes all declared methods of C, including m.
     */
    public boolean excludeOwnNameFromCurrentClass = false;

    /**
     * Add parameters and local variables to the method document. Default false:
     * Algorithm 3, Line 1 indexes only used fields and invoked methods.
     */
    public boolean includeLocalVariables = false;

    /** How many top contributing terms to report per similarity score. */
    public int explainTerms = 5;

    @Override
    public String toString() {
        return "margin=" + minimumMargin
                + ", nestedClasses=" + includeNestedClasses
                + ", excludeOwnName=" + excludeOwnNameFromCurrentClass
                + ", localVariables=" + includeLocalVariables;
    }
}
