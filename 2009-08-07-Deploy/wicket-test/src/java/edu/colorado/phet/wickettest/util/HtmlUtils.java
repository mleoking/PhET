package edu.colorado.phet.wickettest.util;

public class HtmlUtils {
    public static String encode( String s ) {
        /*
        StringBuffer buf = new StringBuffer();
        int len = ( s == null ? -1 : s.length() );

        for ( int i = 0; i < len; i++ ) {
            char c = s.charAt( i );
            if ( c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' ) {
                buf.append( c );
            }
            else {
                buf.append( "&#" + (int) c + ";" );
            }
        }
        return buf.toString();
        */
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
}
