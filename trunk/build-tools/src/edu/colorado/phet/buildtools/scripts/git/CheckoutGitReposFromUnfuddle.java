// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.scripts.git;

import java.io.File;
import java.util.ArrayList;

/**
 * Once Unfuddle is populated with git repos, check them out in the standard locations.
 *
 * @author Sam Reid
 */
public class CheckoutGitReposFromUnfuddle {
    public static void main( String[] args ) {
        ArrayList<File> files = new CreateUnfuddleRepos().getFiles();
        File oldTrunk = new File( "C:\\workingcopy\\phet\\svn\\trunk\\" );
        File target = new File( "C:\\workingcopy\\phet-little-gits" );

        for ( File file : files ) {
            String name = file.getName();
            String command = "git clone git@phet.unfuddle.com:phet/" + name + ".git";
            String rootPath = oldTrunk.getAbsolutePath();
            String newPath = file.getAbsolutePath();
            String relativePath = newPath.substring( rootPath.length() );
            File dest = new File( target, relativePath );
            File destParent = dest.getParentFile();
            System.out.println( "mkdir " + destParent.getAbsolutePath() );
            System.out.println( "cd " + destParent.getAbsolutePath() );
            System.out.println( command );
        }
    }
}
