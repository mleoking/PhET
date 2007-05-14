package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

//todo: add options such as shrink, etc.
public class PhetBuildAllTask extends PhetAllSimTask {
    public final void execute() throws BuildException {
        buildAll( getSimNames() );
    }

    public void buildAll( String[] simNames ) {
        for( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            PhetBuildTask phetBuildTask = new PhetBuildTask();
            phetBuildTask.setProject( simName );
            runTask( phetBuildTask );
        }
    }

}
