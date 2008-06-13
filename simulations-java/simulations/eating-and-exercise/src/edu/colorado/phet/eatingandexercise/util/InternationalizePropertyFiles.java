//package edu.colorado.phet.fitness.util;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.StringTokenizer;
//
//import edu.colorado.phet.build.FileUtils;
//
///**
// * Created by: Sam
// * Jun 5, 2008 at 4:35:51 PM
// */
//public class InternationalizePropertyFiles {
//    public static void main( String[] args ) throws IOException {
//        File file = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations\\fitness\\data\\fitness\\exercise.properties" );
//        String s = FileUtils.loadFileAsString( file );
//        StringTokenizer st = new StringTokenizer( s, "\n" );
//        String newFile = "";
//        while ( st.hasMoreTokens() ) {
//            String line = st.nextToken();
//            System.out.println( "line= " + line );
//            if ( line.trim().length() > 0 && line.indexOf( ":" ) >= 0 ) {
//                String foodName = line.substring( 0, line.indexOf( ":" ) );
//                String unixKey = "";
//                for ( int i = 0; i < foodName.length(); i++ ) {
//                    char ch = foodName.charAt( i );
//                    if ( Character.isLetterOrDigit( ch ) ) {
//                        unixKey += ch;
//                    }
//                    else {
//                        if ( !unixKey.endsWith( "-" ) ) {
//                            unixKey += "-";
//                        }
//                    }
//                }
//
//                while ( unixKey.endsWith( "-" ) ) {
//                    unixKey = unixKey.substring( 0, unixKey.length() - 1 );
//                }
//                System.out.println( "k= " + unixKey );
//                String newLine = line;
//                if ( !foodName.equals( unixKey ) ) {
//                    newLine = FileUtils.replaceAll( line, foodName, unixKey );
//                    s = FileUtils.replaceAll( s, foodName, unixKey );
//                }
//                System.out.println( "newLine:::::::" + newLine );
//                System.out.println( "propertyKey:::" + unixKey + "=" + foodName );
//
//                newFile += unixKey + "=" + foodName + "\n";
//            }
//        }
//        System.out.println( "#################" );
//        System.out.println( s );
//        System.out.println( "#################" );
//        System.out.println( newFile );
//
//    }
//}
