package no.eatools.util;

import org.apache.commons.lang.StringUtils;

/**
 * This is a utility class for converting text strings between CamelCase, Java CONSTANT_CONVENTION,
 * property.name.convention etc.
 *
 * @author AB22273
 * @date 12.nov.2008
 * @since 12.nov.2008 11:37:51
 */
public class Camel {
    /**
     * The String separator used as convention in constant (and Enum) names.
     */
    public static final String CONSTANT_WORD_SEPARATOR = "_";

    public static final String WHITESPACE_REGEX = "[ \t\n]";

    public static final String PROPERTY_NAME_SEPARATOR = ".";

    public static final String ARROWS_AND_HYPHENS = "[\\<\\-\\>]";

    public static final String HYPHEN = "-";

    /**
     * Convert a String from CamelCase to CONSTANT_CONVENTION_CASE.
     * E.g. "AppPropertyFile" becomes "APP_PROPERTY_FILE".
     *
     * @param camelCaseString
     * @return constant type String, all UPPERCASE, words separated by underscore "_"
     */
    public static String toConstantString(String camelCaseString) {
        camelCaseString = camelCaseString.replaceAll(ARROWS_AND_HYPHENS, StringUtils.EMPTY);
        String[] words = StringUtils.splitByCharacterTypeCamelCase(camelCaseString.replaceAll(WHITESPACE_REGEX, StringUtils.EMPTY));
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.toUpperCase()).append(CONSTANT_WORD_SEPARATOR);
        }
        return StringUtils.removeEnd(sb.toString(), CONSTANT_WORD_SEPARATOR);
    }

    /**
     * Convert a String from CONSTANT_CONVENTION_CASE to CamelCase.
     * E.g. "APP_PROPERTY_FILE" becomes "AppPropertyFile".
     *
     * @param constantString
     * @return CamelCase words String, each word Capitalized.
     */
    public static String toCamelCaseString(String constantString) {
        String[] words = constantString.replaceAll(WHITESPACE_REGEX, CONSTANT_WORD_SEPARATOR).split(CONSTANT_WORD_SEPARATOR);

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(StringUtils.capitalize(word.toLowerCase()));
        }
        return sb.toString();
    }

    /**
     * Convert a String from CONSTANT_CONVENTION_CASE to property.convention .
     * E.g. "APP_PROPERTY_FILE" becomes "app.property.file".
     *
     * @param constantString
     * @return
     */
    public static String toPropertyName(String constantString) {
        String[] words = constantString.replaceAll(WHITESPACE_REGEX, CONSTANT_WORD_SEPARATOR).split(CONSTANT_WORD_SEPARATOR);

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!"".equals(word)) {
                sb.append(word.toLowerCase()).append(PROPERTY_NAME_SEPARATOR);
            }
        }
        return StringUtils.removeEnd(sb.toString(), PROPERTY_NAME_SEPARATOR);
    }

    public static String propertyNameAsConstant(String propertyName) {
        String[] words = propertyName.replaceAll(WHITESPACE_REGEX, PROPERTY_NAME_SEPARATOR).split("\\" + PROPERTY_NAME_SEPARATOR);

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (! StringUtils.EMPTY.equals(word)) {
                sb.append(word.toUpperCase()).append(CONSTANT_WORD_SEPARATOR);
            }
        }
        return StringUtils.removeEnd(sb.toString(), CONSTANT_WORD_SEPARATOR);
    }

    public static String camelCaseAsPropertyName(String camelCaseString) {
        String[] words = StringUtils.splitByCharacterTypeCamelCase(camelCaseString.replaceAll(WHITESPACE_REGEX, ""));
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.toLowerCase()).append(PROPERTY_NAME_SEPARATOR);
        }
        return StringUtils.removeEnd(sb.toString(), PROPERTY_NAME_SEPARATOR);
    }

    public static String toHyphenatedCamelCaseString(String constantString) {
        String[] words = constantString.replaceAll(WHITESPACE_REGEX, CONSTANT_WORD_SEPARATOR).split(CONSTANT_WORD_SEPARATOR);

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(StringUtils.capitalize(word.toLowerCase())).append(HYPHEN);
        }
        return StringUtils.removeEnd(sb.toString(), HYPHEN);
    }
}
