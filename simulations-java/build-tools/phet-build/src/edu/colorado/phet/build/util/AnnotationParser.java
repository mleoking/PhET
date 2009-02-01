package edu.colorado.phet.build.util;

import java.util.HashMap;
import java.util.StringTokenizer;

//todo: remove duplicate copy in licensing, build-tools and phet-common
public class AnnotationParser {
    public static class Annotation {
        private String id;
        private HashMap map;

        public Annotation( String id, HashMap map ) {
            this.id = id;
            this.map = map;
        }

        public String getId() {
            return id;
        }

        public HashMap getMap() {
            return map;
        }

        public String toString() {
            return id + ": " + map;
        }

        public String get( String s ) {
            return (String) map.get(s);
        }
    }

    public static Annotation parse( String line ) {
        line = line.trim();
        StringTokenizer st = new StringTokenizer( line, " " );
        HashMap map = new HashMap();
        String id = st.nextToken();
        for ( int index = line.indexOf( '=' ); index >= 0; index = line.indexOf( '=', index + 1 ) ) {
            System.out.println( "Found '=' at: " + index );
            String key = getKey( line, index );
            String value = getValue( line, index );
            map.put( key, value );
        }
        return new Annotation( id, map );
    }

    private static String getValue( String line, int index ) {
        int end = line.indexOf( '=', index + 1 );
        if ( end < 0 ) {
            end = line.length();
        }
        else {
            for ( int i = end; i >= 0; i-- ) {
                if ( line.charAt( i ) == ' ' ) {
                    end = i;
                    break;
                }
            }
        }
        String val = line.substring( index + 1, end );
        System.out.println( "val = " + val );
        return val.trim();
    }

    private static String getKey( String line, int index ) {
        for ( int i = index; i >= 0; i-- ) {
            if ( line.charAt( i ) == ' ' ) {
                String key = line.substring( i, index );
                System.out.println( "key = " + key );
                return key.trim();
            }
        }
        throw new RuntimeException( "No key found" );
    }

    public static void main( String[] args ) {
        Annotation a = AnnotationParser.parse( "test-id name=my name age=3 timestamp=dec 13, 2008" );
        System.out.println( "a = " + a );
    }
}

