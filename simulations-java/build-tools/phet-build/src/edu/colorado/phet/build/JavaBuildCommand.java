package edu.colorado.phet.build;

import java.io.File;

public class JavaBuildCommand {

    public void build( PhetProject phetProject ) {
        clean( phetProject );
        compile( phetProject );
    }

    public void clean( PhetProject phetProject ) {
        deleteRecursive( phetProject.getAntOutputDir() );
    }

    private boolean deleteRecursive( File dir ) {
        //see http://www.exampledepot.com/egs/java.io/DeleteDir.html
        System.out.println( "Deleting: " + dir.getAbsolutePath() );
        if ( dir.isDirectory() ) {
            String[] children = dir.list();
            for ( int i = 0; i < children.length; i++ ) {
                boolean success = deleteRecursive( new File( dir, children[i] ) );
                if ( !success ) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void compile( PhetProject project ) {
        System.out.println( "Started compile" );
        try {
            new PhetBuildCommand( project, new MyAntTaskRunner(), true, project.getDefaultDeployJar() ).execute();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        System.out.println( "Finished compile" );
    }

    public static String getJavacPath( File JDK_HOME ) {
        return new File( new File( JDK_HOME, "bin" ), "javac" ).getAbsolutePath();
    }

}
