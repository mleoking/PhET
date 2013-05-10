package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileFilter;

/**
 * Generate the list of locales to be required for a simulation, so that
 * all provided translations will appear in the minified file so they can be selected
 * dynamically at runtime, see https://github.com/phetsims/ohms-law/issues/16
 *
 * @author Sam Reid
 */
public class ListLocalesToInclude {
    public static void main( String[] args ) {
        System.out.println( "  //Only the strings specified in the config file get loaded unless you explicitly require them,\n" +
                            "  // see https://github.com/phetsims/ohms-law/issues/16" );
        File f = new File( args[0] );//the nls directory
        String simName = f.getParentFile().getName();//like 'ohms-law'
        for ( File file : f.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() && !pathname.getName().equals( "root" );
            }
        } ) ) {
            //"i18n!../nls/fr/ohms-law-strings"
            System.out.println( "  require( \"i18n!../nls/" + file.getName() + "/" + simName + "-strings\" );" );
        }
    }
}
