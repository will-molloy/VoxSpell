package voxspell.tools;

/**
 * Given two strings: generates an object containing the 'common' prefix, suffix and difference between the two strings.
 * <p>
 * Based on the JUnit ComparisonCompactor code: https://searchcode.com/codesearch/view/2593422/
 *
 * @author Will Molloy.
 */
public class StringDifferenceFinder {

    private String correctString;
    private String incorrectString;

    private int prefixLength;
    private int suffixLength;

    public StringDifferenceFinder(String correctString, String incorrectString) {
        this.correctString = correctString;
        this.incorrectString = incorrectString;
        if (stringsAreNotEqual()) {
            findCommonPrefixAndSuffix();
        }
    }

    public String[] getPrefixSuffixAndDelta(boolean correctDelta) {
        if (correctDelta) {
            return extractPrefixSuffixAndDelta(correctString);
        } else {
            return extractPrefixSuffixAndDelta(incorrectString);
        }
    }

    private boolean stringsAreNotEqual() {
        return !correctString.equals(incorrectString);
    }

    private void findCommonPrefixAndSuffix() {
        findCommonPrefix(); // must find prefix length before suffix length
        suffixLength = 0;
        for (; !suffixOverlapsPrefix(); suffixLength++) {
            if (charFromEnd(correctString, suffixLength) != charFromEnd(incorrectString, suffixLength)) {
                break;
            }
        }
    }

    private void findCommonPrefix() {
        prefixLength = 0;
        int end = Math.min(correctString.length(), incorrectString.length());
        for (; prefixLength < end; prefixLength++) {
            if (correctString.charAt(prefixLength) != incorrectString.charAt(prefixLength)) {
                break;
            }
        }
    }

    private boolean suffixOverlapsPrefix() {
        return correctString.length() - suffixLength <= prefixLength || incorrectString.length() - suffixLength <= prefixLength;
    }

    private char charFromEnd(String string, int length) {
        return string.charAt(string.length() - length - 1);
    }

    private String[] extractPrefixSuffixAndDelta(String s) {
        String prefix = correctString.substring(0, prefixLength); // common prefix, doesn't matter what string
        String delta = s.substring(prefixLength, s.length() - suffixLength); // difference
        String suffix = correctString.substring(correctString.length() - suffixLength, correctString.length()); // common suffix

        return new String[]{prefix, suffix, delta};
    }


}
