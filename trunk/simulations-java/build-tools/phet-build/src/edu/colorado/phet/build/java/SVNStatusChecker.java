package edu.colorado.phet.build.java;

import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.ProcessOutputReader;

public class SVNStatusChecker {
    public SVNStatusChecker() {
    }

    public boolean isUpToDate( PhetProject project ) {
        ArrayList args = new ArrayList();
        args.add( "svn" );
        args.add( "status" );
        PhetProject[] projects = project.getAllDependencies();
        for ( int i = 0; i < projects.length; i++ ) {
            args.add( projects[i].getProjectDir().getAbsolutePath() );
        }
        try {
            String[] cmd = (String[]) args.toArray( new String[0] );
            System.out.println( "exec'ing: " + toString( cmd ) );
            Process p = Runtime.getRuntime().exec( cmd );
            ProcessOutputReader pop = new ProcessOutputReader( p.getInputStream() );
            pop.start();
            ProcessOutputReader poe = new ProcessOutputReader( p.getErrorStream() );
            poe.start();
            try {
                p.waitFor();
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            String out = pop.getOutput();
            String err = poe.getOutput();
            if ( out.length() == 0 && err.length() == 0 ) {
                return true;
            }
            else {
                System.out.println( "out=" + out + ", err=" + err );
                System.out.println( "Out of sync with SVN" );
                return false;
            }

        }
        catch( IOException e ) {
            e.printStackTrace();
            return false;
        }

    }

    private String toString( String[] cmd ) {
        String s = "";
        for ( int i = 0; i < cmd.length; i++ ) {
            String s1 = cmd[i];
            s = s + " " + s1;
        }
        return s;
    }
}
