// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.scripts.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

/**
 * Experiment with pulling lots of git repos in parallel to make it run faster
 *
 * @author Sam Reid
 */
public class PullAll {
    public static void main( String[] args ) throws IOException, RefNotFoundException, DetachedHeadException, WrongRepositoryStateException, InvalidRemoteException, InvalidConfigurationException, CanceledException {
        visit( new File( "C:\\workingcopy\\phet-little-gits" ) );
    }

    private static void visit( File directory ) throws IOException, RefNotFoundException, DetachedHeadException, WrongRepositoryStateException, InvalidRemoteException, InvalidConfigurationException, CanceledException {
        for ( final File child : directory.listFiles() ) {
            if ( child.isDirectory() ) {
                visit( child );
            }
            final String name = child.getName();
            if ( name.equals( ".git" ) ) {

                Thread t = new Thread( new Runnable() {
                    public void run() {
                        try {
                            Git git = Git.open( child.getParentFile() );
                            PullResult pullResult = git.pull().call();
                            System.out.println( "pulled on " + child.getParentFile().getName() + ", pullResult = " + pullResult.isSuccessful() + ", " + pullResult.getFetchedFrom() + ", fetch messages: " + pullResult.getFetchResult().getMessages() );
                        }
                        catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                } );
                t.start();

            }
        }
    }
}