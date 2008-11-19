/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build.ant;

import edu.colorado.phet.build.PhetCleanCommand;
import edu.colorado.phet.build.PhetProject;

public class PhetCleanTask extends AbstractPhetBuildTask {
    protected void executeImpl( PhetProject phetProject ) throws Exception {
        new PhetCleanCommand( phetProject, this ).execute();
    }
}
