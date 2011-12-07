// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.scripts.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Some files use whitespace delimiter instead of :, let's find them and upgrade them so we can also add version numbering.
 *
 * @author Sam Reid
 */
public class ShowBuildPropertiesSyntax {
    public static void main( String[] args ) throws IOException {
        File f = new File( "C:\\workingcopy\\phet\\svn\\trunk" );
        visit( f );
    }

    private static void visit( File f ) throws IOException {
        File[] children = f.listFiles();
        for ( File child : children ) {
            if ( child.isDirectory() ) {
                visit( child );
            }
            else {
                if ( child.getName().endsWith( "-build.properties" ) ) {
                    Properties p = new Properties();
                    p.load( new FileInputStream( child ) );
                    checkProperty( child, p, "project.depends.lib" );
                    checkProperty( child, p, "project.depends.source" );
                    checkProperty( child, p, "project.depends.data" );
                }
            }
        }
    }

    private static void checkProperty( File child, Properties p, String key ) {
        String property = p.getProperty( key );
        if ( property != null && property.indexOf( ':' ) < 0 ) {
            StringTokenizer st = new StringTokenizer( property, " " );
            if ( st.countTokens() > 1 ) {
//                System.out.println( child.getParentFile().getName() + ": " + property );
                System.out.println( child.getParentFile().getName() );
            }
        }
    }
}