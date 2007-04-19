package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;

import java.io.File;

/**
 * This class is responsible for building a phet project given the project
 * name. Optionally, the destination jar and shrinking option may be specified.
 */
public class PhetBuildTask extends Task implements AntTaskRunner {
    private volatile String projectName;
    private volatile boolean shrink = true;
    private volatile String destFile;

    // The method executing the task
    public void execute() throws BuildException {
        PhetBuildUtils.antEcho( this, "Building " + projectName + "..." );

        if (destFile == null) {
            destFile = "deploy/" + projectName + ".jar";
        }

        try {
            File projectDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), projectName );

            PhetProject phetProject = new PhetProject( projectDir, projectName, destFile );

            PhetBuildCommand buildCommand = new PhetBuildCommand( phetProject, this, shrink );

            buildCommand.execute();
        }
        catch( Exception e ) {
            throw new BuildException( "A problem occurred while trying to build " + projectName + ".", e );
        }
    }

    public void runTask( Task childTask ) {
        childTask.setProject( getProject() );
        childTask.setLocation( getLocation() );
        childTask.setOwningTarget( getOwningTarget() );
        childTask.init();
        childTask.execute();
    }

    public void setProject( String projectName ) {
        this.projectName = projectName;
    }

    public void setShrink( boolean shrink ) {
        this.shrink = shrink;
    }

    public void setDestFile( String destFile ) {
        this.destFile = destFile;
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
        p.executeTarget( p.getDefaultTarget() );
    }
}