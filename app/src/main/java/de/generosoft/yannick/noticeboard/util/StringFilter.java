package de.generosoft.yannick.noticeboard.util;

public class StringFilter {

    /**
     * returns true if a string is matched by the filter
     * @param string
     * @param filter
     * @return
     */
    public static boolean filter(final String string, final String filter) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        if (filter == null || filter.isEmpty()) {
            return true;
        }
        final String stringLowerCase = string.toLowerCase();
        final String filterLowerCase = filter.toLowerCase();
        return stringLowerCase.contains(filterLowerCase);
    }

}
