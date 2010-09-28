package edu.colorado.phet.website.util;

public class HtmlUtils {

    /**
     * Encodes a string into an HTML-escaped version
     *
     * @param s String to encode
     * @return HTML encoded response
     */
    public static String encode( String s ) {
        if ( s == null ) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int len = s.length();

        for ( int i = 0; i < len; i++ ) {
            char c = s.charAt( i );
            switch( c ) {
                case '&':
                    buf.append( "&amp;" );
                    break;
                case '<':
                    buf.append( "&lt;" );
                    break;
                case '>':
                    buf.append( "&gt;" );
                    break;
                case '"':
                    buf.append( "&quot;" );
                    break;
                case '\'':
                    buf.append( "&apos;" );
                    break;
                default:
                    buf.append( c );
            }
        }
        return buf.toString();
    }

    /**
     * Encodes a string into an HTML-escaped version (attribute version, skips ampersands).
     *
     * Escaping other things is just to appease the Nessus monster. This will cause jibberish for <>'s.
     *
     * @param s String to encode
     * @return HTML encoded response
     */
    public static String encodeForAttribute( String s ) {
        if ( s == null ) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int len = s.length();

        for ( int i = 0; i < len; i++ ) {
            char c = s.charAt( i );
            switch( c ) {
                case '<':
                    buf.append( "&lt;" );
                    break;
                case '>':
                    buf.append( "&gt;" );
                    break;
                case '"':
                    buf.append( "&quot;" );
                    break;
                case '\'':
                    buf.append( "&apos;" );
                    break;
                default:
                    buf.append( c );
            }
        }
        return buf.toString();
    }
}
