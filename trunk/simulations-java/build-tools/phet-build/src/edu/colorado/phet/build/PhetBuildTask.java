package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;

import java.io.File;

public class PhetBuildTask extends Task implements AntTaskRunner {
    private String projectName;
    private boolean shrink = true;
    private String destFile;

    // The method executing the task
    public void execute() throws BuildException {
        PhetBuildUtils.antEcho( this, "Building: " + projectName );

        if (destFile == null) {
            destFile = "deploy/" + projectName + ".jar";
        }

        try {
            PhetProject phetProject = new PhetProject( PhetBuildUtils.resolveProject( getBaseDir(), projectName ), projectName, destFile );

            PhetBuildCommand manager = new PhetBuildCommand( phetProject, this, shrink );

            manager.execute();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void runTask( Task child ) {
        child.setProject( getProject() );
        child.setLocation( getLocation() );
        child.setOwningTarget( getOwningTarget() );
        child.init();
        child.execute();
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

    public File getBaseDir() {
        return getProject().getBaseDir();
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