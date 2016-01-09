package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.buildtools.html5.JavaScriptUtils.escapeDoubleQuoteJS;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.mkString;

/**
 * Converts .properties files to the i18n format used by requirejs's i18n plugin.
 * See http://requirejs.org/docs/api.html#i18n
 *
 * @author Sam Reid
 */
public class PropertiesToRequireJSI18n {
    public static void main( final String[] args ) throws IOException {

        // First command-line argument is the localization directory, such as "C:\workingcopy\phet\svn-1.7\trunk\simulations-java\simulations\energy-skate-park\data\energy-skate-park\localization"
        File source = new File( args[0] );

        // Second command-line argument is the sub-directory of babel where the strings should go, such as "C:\workingcopy\phet\git\babel\states-of-matter"
        // Note that the name of this directory will be used to name the string files! Create it if it doesn't exist (it probably doesn't exist)
        File babelDir = new File( args[1] );

        if ( !source.exists() ) {
            throw new RuntimeException( "Could not find source directory!" );
        }

        if ( !babelDir.exists() ) {
            throw new RuntimeException( "Could not find babel directory!" );
        }

        for ( final File file : source.listFiles( new FilenameFilter() {
            public boolean accept( final File dir, final String name ) {
                return name.endsWith( ".properties" ) && name.contains( "-strings" );
            }
        } ) ) {
            final boolean english = file.getName().indexOf( '_' ) < 0;
            final Properties p = new Properties() {{
                load( new FileInputStream( file ) );
            }};

            String data = mkString( map( p.keySet(), new Function1<Object, String>() {
                public String apply( Object keyObject ) {
                    return "  \"" + escapeDoubleQuoteJS( keyObject.toString() ) + "\": {\n" +
                           "    \"value\": \"" + escapeDoubleQuoteJS( p.get( keyObject ).toString() ) + "\"\n" +
                           "  }";
                }
            } ), ",\n" );

            String output = "{\n" + data + "\n}\n";

            String locale;
            if ( english ) {
                locale = "en";
            }
            else {
                final String tail = file.getName().substring( file.getName().indexOf( "_" ) + 1 );
                locale = tail.substring( 0, tail.indexOf( '.' ) );
            }

            String filename = babelDir.getName() + "-strings_" + locale + ".json";
            FileUtils.writeString( new File( babelDir, filename ), output );
        }
    }
}