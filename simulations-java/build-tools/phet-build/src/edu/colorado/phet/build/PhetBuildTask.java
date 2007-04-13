package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;

import java.io.File;
import java.util.ArrayList;

public class PhetBuildTask extends Task {
    private String projectName;

    // The method executing the task
    public void execute() throws BuildException {
        output( "Executing: " + projectName );
        buildProject();
    }

    private void buildProject() {
        output( "in build project" );
    }

    public void output( String string ) {
        System.out.println( string );
        echo( string );
    }

    private void echo( String message ) {
        Echo echo = new Echo();
        echo.setProject( getProject() );
        echo.setLocation( getLocation() );
        echo.setOwningTarget( getOwningTarget() );
        echo.setTaskName( getTaskName() );
        echo.setMessage( message );
        echo.init();
        try {
            echo.execute();
        }
        catch( NullPointerException np ) {

        }
    }

    public void setProject( String projectName ) {
        this.projectName = projectName;
    }

    public static class PhetProject {
        ArrayList sourcePaths = new ArrayList();
        ArrayList libPaths = new ArrayList();
        ArrayList dataPaths = new ArrayList();
        ArrayList projectDependencies = new ArrayList();
    }

    public static void main( String[] args ) {
        File buildFile = new File( "build.xml" );
        Project p = new Project();
        p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
        p.init();
        ProjectHelper.configureProject( p, buildFile );
        p.executeTarget( p.getDefaultTarget() );
    }
}