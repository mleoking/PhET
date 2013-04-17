package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Converts .properties files to the i18n format used by requirejs's i18n plugin.
 * See http://requirejs.org/docs/api.html#i18n
 *
 * @author Sam Reid
 */
public class PropertiesToRequireJSI18n {
    public static void main( final String[] args ) throws IOException {

        //Source is the localization directory, such as "C:\workingcopy\phet\svn-1.7\trunk\simulations-java\simulations\energy-skate-park\data\energy-skate-park\localization"
        File source = new File( args[0] );

        //Destination is the target nls root, such as "C:\workingcopy\phet\svn-1.7\trunk\simulations-html5\simulations\energy-skate-park-easeljs-jquerymobile\src\app\nls"
        File destination = new File( args[1] );

        for ( final File file : source.listFiles( new FilenameFilter() {
            public boolean accept( final File dir, final String name ) {
                return name.endsWith( ".properties" ) && name.contains( "-strings" );
            }
        } ) ) {
            final boolean english = file.getName().indexOf( '_' ) < 0;
            Properties p = new Properties() {{
                load( new FileInputStream( file ) );
            }};
            String output = //"//Conversion from " + file.getAbsolutePath() + "\n" +
                    "define( {\n";
            if ( english ) {
                output += "            \"root\": {\n";
            }

            for ( Object key : p.keySet() ) {
                String prefix = english ? "                \"" : "            \"";
                output += prefix + key + "\": \"" + escape( p.get( key ).toString() ) + "\",\n";
            }

            //Remove the last comma
            output = output.substring( 0, output.lastIndexOf( ',' ) ) + output.substring( output.lastIndexOf( ',' ) + 1 );

            if ( english ) {
                output += "            }";


                //list the files
                final ArrayList<String> others = getNonEnglishLocales( source );
                for ( String other : others ) {
                    output += ",\n            \"" + other + "\": true";
                }
                output += "\n";

            }
            output += "        } );";

            System.out.println( output );
            System.out.println();
            System.out.println();
            System.out.println();

            String a = file.getName().substring( 0, file.getName().indexOf( "-strings" ) );
            String filename = a + "-strings.js";

            if ( !english ) {
                final String tail = file.getName().substring( file.getName().indexOf( "_" ) + 1 );
                String localeAndCountry = tail.substring( 0, tail.indexOf( '.' ) );
                File outputDir = new File( destination, localeAndCountry.toLowerCase().replace( "_", "-" ) );
                outputDir.mkdirs();
                System.out.println( "outputDir = " + outputDir );
                FileUtils.writeString( new File( outputDir, filename ), output );
            }
            else {
                destination.mkdirs();
                FileUtils.writeString( new File( destination, filename ), output );
            }
        }
    }

    private static String escape( final String s ) {
        //Replace " with \".  May need to add other escapes later on.
        return s.replace( "\"", "\\\"" ).replace( "\n", "\\n" );
    }

    public static ArrayList<String> getNonEnglishLocales( File source ) throws IOException {
        ArrayList<String> strings = new ArrayList<String>();

        for ( final File file : source.listFiles( new FilenameFilter() {
            public boolean accept( final File dir, final String name ) {
                return name.endsWith( ".properties" );
            }
        } ) ) {
            final boolean english = file.getName().indexOf( '_' ) < 0;

            if ( !english ) {
                final String tail = file.getName().substring( file.getName().indexOf( "_" ) + 1 );
                String localeAndCountry = tail.substring( 0, tail.indexOf( '.' ) );
                strings.add( localeAndCountry.toLowerCase().replace( "_", "-" ) );
            }
        }
        return strings;
    }
}