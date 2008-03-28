/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.phetgraphicsdemo;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import java.io.IOException;

/**
 * PhetgraphicsDemoApplication demonstrates how registration point, location 
 * and transforms can be combined to create "self-contained behaviors" that
 * can be combined in composite graphics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetgraphicsDemoApplication extends PhetApplication {

    public PhetgraphicsDemoApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) throws IOException {
        super( args, title, description, version, frameSetup );
        TestModule module = new TestModule();
        addModule( module );
    }
    
    public static void main( String[] args ) throws IOException {

        String title = "Phetgraphics Demo Application";
        String description = "demonstrates semantics of location and registration point";
        String version = "0.00.01";
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

        PhetApplication app = new PhetgraphicsDemoApplication( args, title, description, version, frameSetup );
        app.startApplication();
    }
}
