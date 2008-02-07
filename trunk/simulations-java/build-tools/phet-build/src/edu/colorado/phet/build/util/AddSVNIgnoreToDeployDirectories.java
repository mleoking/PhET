package edu.colorado.phet.build.util;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Feb 6, 2008 at 11:07:48 PM
 */
public class AddSVNIgnoreToDeployDirectories {
    public static void main( String[] args ) throws IOException {
        File baseDir = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" );
        File patternFile = new File( "C:\\Users\\Sam\\Desktop\\pattern.txt" );
        PhetProject[] p = PhetProject.getAllProjects( baseDir );
        for ( int i = 0; i < p.length; i++ ) {
            String svnCommand = "svn propset svn:ignore –F " + patternFile.getAbsolutePath() + " " + p[i].getDefaultDeployDir().getAbsolutePath();
            System.out.println( "Running command: " + svnCommand );
//            Runtime.getRuntime().exec( svnCommand );
        }
    }
}
