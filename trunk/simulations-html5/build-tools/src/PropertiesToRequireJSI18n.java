package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
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
            @Override public boolean accept( final File dir, final String name ) {
                return name.endsWith( ".properties" );
            }
        } ) ) {
            Properties p = new Properties() {{
                load( new FileInputStream( file ) );
            }};
            String output = "//Conversion from " + file.getAbsolutePath() + "\n" +
                            "define( {\n" +
                            "            \"root\": {\n";

            for ( Object key : p.keySet() ) {
                output += "                \"" + key + "\": \"" + p.get( key ) + "\",\n";
            }

            //Remove the last comma
            output = output.substring( 0, output.lastIndexOf( ',' ) ) + output.substring( output.lastIndexOf( ',' ) + 1 );

            output += "            }\n" +
                      "        } );";

            System.out.println( output );
            System.out.println();
            System.out.println();
            System.out.println();

            String a = file.getName().substring( 0, file.getName().indexOf( "-strings" ) );
            String filename = a + "-strings.js";

            final boolean english = file.getName().indexOf( '_' ) < 0;
            if ( !english ) {
                final String tail = file.getName().substring( file.getName().indexOf( "_" ) + 1 );
                String localeAndCountry = tail.substring( 0, tail.indexOf( '.' ) );
                File outputDir = new File( destination, localeAndCountry );
                outputDir.mkdirs();
                System.out.println( "outputDir = " + outputDir );
                FileUtils.writeString( new File( outputDir, filename ), output );
            }
            else {
                FileUtils.writeString( new File( destination, filename ), output );
            }
        }
    }
}