package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;

import java.util.Properties;

public class PhetBuildTask extends Task {
    private String projectName;

    // The method executing the task
    public void execute() throws BuildException {
        System.out.println( "Executing: " + projectName );

        Echo echo = new Echo();
        echo.setProject( getProject() );
        echo.setLocation( getLocation() );
        echo.setOwningTarget( getOwningTarget() );
        echo.setTaskName( getTaskName() );
        echo.setMessage( "Started building: " + projectName );
        echo.init();
        echo.execute();
    }

    public void setProject( String projectName ) {
        this.projectName = projectName;
    }

    public static void main( String[] args ) {
        
    }
}