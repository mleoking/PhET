// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.scripts.git;

import java.io.File;
import java.util.ArrayList;

/**
 * Use KDE's svn2git utility to create repositories
 *
 * @author Sam Reid
 */
public class GenerateRepos {
    public static void main( String[] args ) {
        final ArrayList<File> all = new ArrayList<File>();
        final File trunk = new File( "C:\\workingcopy\\phet\\svn\\trunk" );
        new GenerateRepos().visit( trunk, all );
        System.out.println( "all = " + all );
        for ( File file : all ) {
            System.out.println( file.getAbsolutePath() );
        }

        System.out.println( "create repository " + "misc" );
        System.out.println( "end repository" );
        System.out.println( "" );

        for ( File file : all ) {
            System.out.println( "create repository " + file.getName() );
            System.out.println( "end repository" );
            System.out.println( "" );
        }

        for ( File file : all ) {
            System.out.println( "match /trunk" + getRelativePath( trunk, file ) + "/" );
            System.out.println( "\trepository " + file.getName() );
            System.out.println( "\tbranch master" );
            System.out.println( "end match" );
            System.out.println();
        }

        System.out.println( "match /" );
        System.out.println( "\trepository misc" );
        System.out.println( "\tbranch master" );
        System.out.println( "end match" );
    }

    private static String getRelativePath( File trunk, File file ) {
        String a = trunk.getAbsolutePath();
        String b = file.getAbsolutePath();
        return b.substring( a.length() ).replace( '\\', '/' );
    }

    private void visit( File dir, ArrayList<File> all ) {
        if ( isModuleRoot( dir ) ) {
            System.out.println( "found: " + dir );
            all.add( dir );
        }
//        if ( all.size() > 4 ) {
//            return;
//        }
        for ( File file : dir.listFiles() ) {
            if ( file.isDirectory() ) {
//                System.out.println( "Visiting: " + file );
                visit( file, all );
            }
        }
    }

    private boolean isModuleRoot( File dir ) {
        return new File( dir, dir.getName() + ".iml" ).exists();
    }
}