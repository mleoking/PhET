package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;

import java.io.File;
import java.io.IOException;

public class PhetBuildTask extends Task {
    private String projectName;
    private boolean shrink = true;

    // The method executing the task
    public void execute() throws BuildException {
        echo( "Building: " + projectName );
        try {
            buildSimulation();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void buildSimulation() throws IOException {
        PhetProject phetProject = new PhetProject( this, new File( getProject().getBaseDir(), "simulations" ), projectName );
        phetProject.buildAll( shrink );
    }

    public void runTask( Task child ) {
        child.setProject( getProject() );
        child.setLocation( getLocation() );
        child.setOwningTarget( getOwningTarget() );
        child.init();
        child.execute();
    }

    void echo( String message ) {
        Echo echo = new Echo();
        echo.setMessage( message );
        runTask( echo );
    }

    public void setProject( String projectName ) {
        this.projectName = projectName;
    }

    public void setShrink( boolean shrink ) {
        this.shrink = shrink;
    }

    public File getBaseDir() {
        return project.getBaseDir();
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