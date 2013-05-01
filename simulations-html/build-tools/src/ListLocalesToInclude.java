package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileFilter;

/**
 * Created with IntelliJ IDEA.
 * User: Sam
 * Date: 5/1/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListLocalesToInclude {
    public static void main( String[] args ) {
        System.out.println( "  //Only the strings specified in the config file get loaded unless you explicitly require them,\n" +
                            "  // see https://github.com/phetsims/ohms-law/issues/16" );
        File f = new File( args[0] );//the nls directory
        String simName = args[1];//like 'ohms-law-strings'
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
