package edu.colorado.phet.phetgraphicsdemo;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import java.io.IOException;

/**
 * PhetgraphicsDemoApplication demonstrates how registration point, location 
 * and transforms can be combined to create "self-contained behaviors" that
 * can be combined in composite graphics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhetgraphicsDemoApplication extends NonPiccoloPhetApplication {

    public PhetgraphicsDemoApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) throws IOException {
        super( args, title, description, version, frameSetup );
        TestModule module = new TestModule();
        addModule( module );
    }
    
    public static void main( String[] args ) throws IOException {

        String title = "Test Location";
        String description = "Demonstrates semantics of phetgraphics location";
        String version = "0.1";
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

        NonPiccoloPhetApplication app = new PhetgraphicsDemoApplication( args, title, description, version, frameSetup );
        app.startApplication();
    }
}
