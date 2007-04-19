/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.File;

public abstract class AbstractPhetBuildTask extends Task implements AntTaskRunner {
    private volatile String projectName;

    // The method executing the task
    public final void execute() throws BuildException {
        PhetBuildUtils.antEcho( this, "Building " + projectName + "..." );

        try {
            File projectDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), projectName );

            PhetProject phetProject = new PhetProject( projectDir, projectName );

            executeImpl( phetProject );
        }
        catch( Exception e ) {
            throw new BuildException( "A problem occurred while trying to build " + projectName + ".", e );
        }
    }

    protected abstract void executeImpl( PhetProject phetProject ) throws Exception;

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
}
