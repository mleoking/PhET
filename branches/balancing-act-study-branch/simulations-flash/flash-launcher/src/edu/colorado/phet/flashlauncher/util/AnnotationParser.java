package edu.colorado.phet.flashlauncher.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


//TODO: remove duplicate copy in licensing, build-tools and phet-common (and now flashlauncher)
public class AnnotationParser {
    public static Annotation[] getAnnotations( String text ) {
        StringTokenizer st = new StringTokenizer( text, "\n" );
        ArrayList annotations = new ArrayList();
        while ( st.hasMoreTokens() ) {
            String tok = st.nextToken().trim();
            if ( !tok.startsWith( "#" ) ) {
                annotations.add( parse( tok ) );
            }
        }
        return (Annotation[]) annotations.toArray( new Annotation[annotations.size()] );
    }

    public static class Annotation {
        private String id;
        private HashMap map;
        private ArrayList keyOrdering;

        public Annotation( String id, HashMap map, ArrayList keyOrdering ) {
            this.id = id;
            this.map = map;
            this.keyOrdering = keyOrdering;
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
            return (String) map.get( s );
        }

        public ArrayList getKeyOrdering() {
            return keyOrdering;
        }
    }

    public static Annotation parse( String line ) {
        line = line.trim();
        StringTokenizer st = new StringTokenizer( line, " " );
        HashMap map = new HashMap();
        String id = st.nextToken();
        ArrayList keyOrdering = new ArrayList();
        for ( int index = line.indexOf( '=' ); index >= 0; index = line.indexOf( '=', index + 1 ) ) {
//            System.out.println( "Found '=' at: " + index );
            String key = getKey( line, index );
            String value = getValue( line, index );
            map.put( key, value );
            keyOrdering.add( key );
        }
        return new Annotation( id, map, keyOrdering );
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
//        System.out.println( "val = " + val );
        return val.trim();
    }

    private static String getKey( String line, int index ) {
        for ( int i = index; i >= 0; i-- ) {
            if ( line.charAt( i ) == ' ' ) {
                String key = line.substring( i, index );
//                System.out.println( "key = " + key );
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