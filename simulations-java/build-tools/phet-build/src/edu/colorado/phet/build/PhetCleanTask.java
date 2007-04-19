/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

public class PhetCleanTask extends AbstractPhetBuildTask {
    protected void executeImpl( PhetProject phetProject ) throws Exception {
        new PhetCleanCommand( phetProject, this ).execute();       
    }
}
