/* Copyright 2009, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.util.ArrayList;

/**
 * Validates a target (translated) string by comparing it to a source (English) string.
 * To be valid, the target string must contain all of the MessageFormat placeholders 
 * that are present in the English string.  A subset of MessageFormat syntax is supported,
 * eg, "{0} went to the {1} to buy {2}."
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MessageFormatValidator {

    private static final char PLACEHOLDER_START_CHAR = '{';
    private static final char PLACEHOLDER_END_CHAR = '}';
    private static final int UNDEFINED_INDEX = -1;

    private final String key;
    private final String sourceString;
    private final ArrayList<String> placeholders;

    /**
     * Constructor.
     * @param key localization lookup key
     * @param sourceString source string used to validate target strings
     */
    public MessageFormatValidator( String key, String sourceString ) {
        super();
        this.key = key;
        this.sourceString = sourceString;
        placeholders = parsePlaceholders( sourceString );
    }

    /*
     * Parses a string and extracts a list of MessageFormat placeholders.
     */
    private static ArrayList<String> parsePlaceholders( String s ) {

        ArrayList<String> placeholders = new ArrayList<String>();
        char[] chars = s.toCharArray();
        int beginIndex = UNDEFINED_INDEX;
        int endIndex = UNDEFINED_INDEX;
        for ( int i = 0; i < chars.length; i++ ) {
            char c = chars[i];
            if ( c == PLACEHOLDER_START_CHAR && beginIndex == UNDEFINED_INDEX ) {
                beginIndex = i;
            }
            else if ( c == PLACEHOLDER_END_CHAR && beginIndex != UNDEFINED_INDEX ) {
                endIndex = i;
            }

            if ( beginIndex != UNDEFINED_INDEX && endIndex != UNDEFINED_INDEX ) {
                String placeholder = s.substring( beginIndex, endIndex + 1 );
                placeholders.add( placeholder );
                beginIndex = endIndex = UNDEFINED_INDEX;
            }
        }
        return placeholders;
    }

    /**
     * Gets the localization lookup key for this string.
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the source (English) string used to validate strings.
     * @return
     */
    public String getSourceString() {
        return sourceString;
    }

    /**
     * Verifies that a specified string contains all of the MessageFormat placeholders that
     * were in the source string.  Returns a list of any placeholders that were missing, or
     * null if all placeholders are present.  Does not check for the presence of additional
     * erroneous placeholders in the target string.
     * 
     * @param targetString
     * @return null (valid) or list of missing placeholders (invalid)
     */
    public ArrayList<String> validate( String targetString ) {
        ArrayList<String> missingPlaceholders = null;
        for ( String placeholder : placeholders ) {
            if ( !targetString.contains( placeholder ) ) {
                if ( missingPlaceholders == null ) {
                    missingPlaceholders = new ArrayList<String>();
                }
                missingPlaceholders.add( placeholder );
            }
        }
        return missingPlaceholders;
    }

    /* test */
    public static void main( String[] args ) {
        MessageFormatValidator v1 = new MessageFormatValidator( "key", "{0} is a {1} or a {2}." );
        MessageFormatValidator v2 = new MessageFormatValidator( "key", "hello world" );
        System.out.println( v1.validate( "{0} foo is a {1} bar or a {2} baz" ) ); // null
        System.out.println( v1.validate( "{0} foo is a bar or a {2} baz" ) ); // [{1}]
        System.out.println( v1.validate( "{0} foo is a {1} bar or a {3} baz" ) ); // [{2}]
        System.out.println( v2.validate( "{0} foo is a {1} bar or a {2} baz" ) ); // null
        System.out.println( v2.validate( "{0} foo is a bar or a {2} baz" ) ); // null
        System.out.println( v2.validate( "{0} foo is a {1} bar or a {3} baz" ) ); // null
    }
}
