package moveMethodRefactoring.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Turns a Java identifier into index terms (Section 4.4):
 * splits on non-letters, camelCase and PascalCase boundaries (including
 * acronyms such as "XMLParser" -> "xml", "parser"), lower-cases each word,
 * drops one-letter words and Java keywords, and applies Porter stemming.
 */
public final class IdentifierTokenizer {

    private static final int MIN_WORD_LENGTH = 2;

    /** Java reserved words and literals; a word such as "new" in "newValue" is not used as a term. */
    static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while",
            "true", "false", "null", "var", "record", "yield", "sealed", "permits"));

    private final PorterStemmer stemmer = new PorterStemmer();

    public List<String> tokenize(String identifier) {
        List<String> terms = new ArrayList<>();
        if (identifier == null) return terms;

        for (String word : splitWords(identifier)) {
            String lower = word.toLowerCase();
            if (lower.length() < MIN_WORD_LENGTH || JAVA_KEYWORDS.contains(lower)) continue;
            terms.add(stemmer.stem(lower));
        }
        return terms;
    }

    /** Splits an identifier into words without changing their case. */
    static List<String> splitWords(String identifier) {
        List<String> words = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int n = identifier.length();

        for (int i = 0; i < n; i++) {
            char c = identifier.charAt(i);
            if (!Character.isLetter(c)) {          // '_', '$', digits, spaces
                flush(current, words);
                continue;
            }
            if (current.length() > 0 && Character.isUpperCase(c)) {
                char prev = current.charAt(current.length() - 1);
                boolean nextIsLower = i + 1 < n && Character.isLowerCase(identifier.charAt(i + 1));
                // fooBar -> foo|Bar ; XMLParser -> XML|Parser
                if (Character.isLowerCase(prev) || nextIsLower) {
                    flush(current, words);
                }
            }
            current.append(c);
        }
        flush(current, words);
        return words;
    }

    private static void flush(StringBuilder sb, List<String> out) {
        if (sb.length() > 0) {
            out.add(sb.toString());
            sb.setLength(0);
        }
    }
}
