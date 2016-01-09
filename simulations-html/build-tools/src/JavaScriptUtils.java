// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.buildtools.html5;

/**
 * @author Jonathan Olson
 */
public class JavaScriptUtils {
    public static String escapeSingleQuoteJS( String str ) {
        return escapeJS( str, '\'' );
    }

    public static String escapeDoubleQuoteJS( String str ) {
        return escapeJS( str, '"' );
    }

    /**
     * Escapes a string into a format where it can be placed within a single-quoted or double-quoted string in JS (depending
     * on the quote char passed in.
     *
     * @param str   - The content string
     * @param quote - Character used as a quote
     * @return
     */
    public static String escapeJS( String str, char quote ) {
        String result = "";

        char lastChr = 0;
        for ( int i = 0; i < str.length(); i++ ) {
            char chr = str.charAt( i );

            switch( chr ) {
                // single quote escaping
                case '\'':
                    if ( quote == chr ) {
                        result += "\\'";
                    }
                    else {
                        result += chr;
                    }
                    break;
                // double quote escaping
                case '"':
                    if ( quote == chr ) {
                        result += "\\\"";
                    }
                    else {
                        result += chr;
                    }
                    break;
                // avoid </
                case '/':
                    if ( lastChr == '<' ) {
                        result += "\\/";
                    }
                    else {
                        result += "/";
                    }
                    break;
                // backslash escapes
                case '\\':
                    result += "\\\\";
                    break;
                case '\b':
                    result += "\\b";
                    break;
                case '\t':
                    result += "\\t";
                    break;
                case '\n':
                    result += "\\n";
                    break;
                case '\f':
                    result += "\\f";
                    break;
                case '\r':
                    result += "\\r";
                    break;
                default:
                    // unicode escape where necessary
                    if ( chr < ' ' || ( chr >= '\u0080' && chr < '\u00a0' ) || ( chr >= '\u2000' && chr < '\u2100' ) ) {
                        String hex = Integer.toHexString( chr );
                        while ( hex.length() < 4 ) {
                            hex = "0" + hex;
                        }
                        result += "\\u" + hex;
                    }
                    // otherwise add it directly
                    else {
                        result += chr;
                    }
            }

            lastChr = chr;
        }
        return result;
    }
}