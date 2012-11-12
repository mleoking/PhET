package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

/**
 * Converts .properties files to the i18n format used by requirejs's i18n plugin.
 * See http://requirejs.org/docs/api.html#i18n
 *
 * @author Sam Reid
 */
public class PropertiesToRequireJSI18n {
    public static void main( final String[] args ) throws IOException {
        File root = new File( args[0] );
        for ( final File file : root.listFiles( new FilenameFilter() {
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
        }
    }
}