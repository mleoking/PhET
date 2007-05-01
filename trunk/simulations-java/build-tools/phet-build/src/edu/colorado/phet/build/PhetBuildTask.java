package edu.colorado.phet.build;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import java.io.File;

/**
 * This class is responsible for building a phet project given the project
 * name. Optionally, the destination jar and shrinking option may be specified.
 */
public class PhetBuildTask extends AbstractPhetBuildTask {
    private volatile boolean shrink = true;
    private String dest = null;

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        File destFile = dest != null ? new File( phetProject.getDir(), dest ) : phetProject.getDefaultDeployJar();

        PhetBuildCommand buildCommand = new PhetBuildCommand( phetProject, this, shrink, destFile );

        buildCommand.execute();
    }

    public void setShrink( boolean shrink ) {
        this.shrink = shrink;
    }

    public void setDestFile( String destFile){
        this.dest = destFile;
    }

    /*
   http://www-128.ibm.com/developerworks/websphere/library/techarticles/0502_gawor/0502_gawor.html

   See here for a discussion of how to have an ant script use this task immediately after building it:
   http://ant.apache.org/manual/develop.html
    */
    public static void main( String[] args ) {
        File buildFile = new File( "build.xml" );
        Project p = new Project();
        p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
        p.init();
        ProjectHelper.configureProject( p, buildFile );
        p.executeTarget( "build_tr" );
    }
}