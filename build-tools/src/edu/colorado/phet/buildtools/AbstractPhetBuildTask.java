/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools;

import java.io.File;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.buildtools.util.PhetBuildUtils;
import edu.colorado.phet.buildtools.java.JavaSimulationProject;

public abstract class AbstractPhetBuildTask extends AbstractPhetTask {
    private volatile String projectName;

    // The method executing the task
    public final void execute() throws BuildException {
        echo( "Executing " + getClass().getName()+" for "+projectName + "..." );

        try {
            File projectDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), projectName );

            PhetProject phetProject = new JavaSimulationProject( projectDir, projectName );

            executeImpl( phetProject );
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new BuildException( "A problem occurred while trying to build " + projectName + ".", e );
        }
    }

    protected abstract void executeImpl( PhetProject phetProject ) throws Exception;

    public void setProject( String projectName ) {
        this.projectName = projectName;
    }
}
