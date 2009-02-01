package edu.colorado.phet.licensing;

//todo: remove duplicate copy in licensing, build-tools and phet-common
public class AnnotationParser {
    public static String getAttribute( String param, String attributes, String[] keys ) {
//        attributes += " suffix=dummyvalue";//dummy key value pair to simplify parsing
        String key = param + "=";
        int index = attributes.indexOf( key );
        if ( index < 0 ) {
            return null;
        }
        else {
            String remainder = attributes.substring( index + key.length() ).trim();
            String substring = parseNext( remainder, keys );
            return substring.trim();
        }
    }

    public static String parseNext( String remainder, String[] keys ) {
        int next = Integer.MAX_VALUE;
        for ( int i = 0; i < keys.length; i++ ) {
            int nextIndex = remainder.indexOf( keys[i] + "=" );
            if ( nextIndex >= 0 && nextIndex < next ) {
                next = nextIndex;
            }
        }
        if ( next == Integer.MAX_VALUE ) {//was the last key-value pair
            next = remainder.length();
        }
        return remainder.substring( 0, next );
    }
}
