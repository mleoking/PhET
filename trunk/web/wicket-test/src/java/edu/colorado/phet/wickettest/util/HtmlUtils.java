package edu.colorado.phet.wickettest.util;

public class HtmlUtils {
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
}
