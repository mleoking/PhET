package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class PhetBuildTask extends Task {
    private String msg;

    // The method executing the task
    public void execute() throws BuildException {
        System.out.println(msg);
    }

    public void setProject(String projectName){
        
    }
}