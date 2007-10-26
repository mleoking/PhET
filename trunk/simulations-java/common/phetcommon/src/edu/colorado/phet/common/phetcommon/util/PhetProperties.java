package edu.colorado.phet.common.phetcommon.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Created by: Sam
 * Oct 26, 2007 at 11:38:55 AM
 */
public class PhetProperties {
    ArrayList properties = new ArrayList();

    public PhetProperties( File propertyFile ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( propertyFile ) );
        Properties p = new Properties();
        p.load( new FileInputStream( propertyFile ) );
        for ( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            line = line.trim();
            if ( line.startsWith( "#" ) ) {//ignore comments
            }
            else {
                int equalsSignIndex = line.indexOf( '=' );
                if ( equalsSignIndex >= 1 ) {
                    String key = line.substring( 0, equalsSignIndex ).trim();
                    if ( p.containsKey( key ) ) {

                        if ( containsKey( key ) ) {
                            System.out.println( "Found duplicate key " + key + " in file: " + propertyFile.getAbsolutePath() );
                        }
                        else {
                            //String value = line.substring( equalsSignIndex + 1 ).trim();//fails for multiline properties, escaped characters, etc
                            properties.add( new Entry( key, p.getProperty( key ) ) );
                        }
                    }
                }
            }
        }
        int javaPropertyKeyCount = p.keySet().size();
        int ourKeyCount = properties.size();
        if ( javaPropertyKeyCount != ourKeyCount ) {
            if ( javaPropertyKeyCount < ourKeyCount ) {
                System.out.println( "Java missed " + ( ourKeyCount - javaPropertyKeyCount ) + " properties" );
                for ( int i = 0; i < properties.size(); i++ ) {
                    Entry entry = (Entry) properties.get( i );
                    if ( !p.containsKey( entry.getKey() ) ) {
                        System.out.println( "Java is missing key: " + entry.getKey() );
                    }
                }
            }
            else {
                System.out.println( "We missed " + ( javaPropertyKeyCount - ourKeyCount ) + " properties" );
                Set keys = p.keySet();
                for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    if ( !containsKey( key ) ) {
                        System.out.println( "We are missing key=" + key +" java property value="+p.get(key));
                    }

                }
            }

            new RuntimeException( "Wrong key size for file: " + propertyFile.getAbsolutePath() + ", propertyKeys=" + javaPropertyKeyCount + ", readKeys=" + ourKeyCount ).printStackTrace();
        }
    }

    private boolean containsKey( String key ) {
        for ( int i = 0; i < properties.size(); i++ ) {
            Entry entry = (Entry) properties.get( i );
            if ( entry.getKey().equals( key ) ) {
                return true;
            }
        }
        return false;
    }

    private static class Entry {
        String key;
        String value;

        private Entry( String key, String value ) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return key + " = " + value;
        }
    }

    public String toString() {
        return properties.toString();
    }

    private String toStringMultiLine() {
        StringBuffer str = new StringBuffer();
        for ( int i = 0; i < properties.size(); i++ ) {
            Entry entry = (Entry) properties.get( i );
            str.append( entry );
            str.append( System.getProperty( "line.separator" ) );
        }
        return str.toString();
    }

    public static void main( String[] args ) throws IOException {
//        PhetProperties phetProperties = new PhetProperties( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\common\\phetcommon\\data\\phetcommon\\localization\\phetcommon-strings.properties" ) );
//        PhetProperties phetProperties = new PhetProperties( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations\\ohm-1d\\data\\ohm-1d\\localization\\ohm-1d-strings_es.properties" ) );
//        PhetProperties phetProperties = new PhetProperties( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations\\bound-states\\data\\bound-states\\localization\\bound-states-strings_es.properties" ) );
//        System.out.println( "phetProperties = " + phetProperties );
//        System.out.println( "phetProperties = \n" + phetProperties.toStringMultiLine() );
        searchAndTest( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" ) );
    }

    private static void searchAndTest( File dir ) throws IOException {
        File[] f = dir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            if ( file.isDirectory() ) {
                searchAndTest( file );
            }
            else {
                if ( file.getName().endsWith( ".properties" ) ) {
                    PhetProperties phetProperties = new PhetProperties( file );
                    //assertion shouldn't fail
                    System.out.println( "Safely Loaded properties file: " + file.getAbsolutePath() );
                }
            }
        }
    }
}
