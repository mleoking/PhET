package edu.colorado.phet.phetgraphicsdemo;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import java.io.IOException;

/**
 * TestApplication is a test application that demonstrates proposed changes
 * to the semantics of PhetGraphic's location.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestApplication extends NonPiccoloPhetApplication {

    public TestApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) throws IOException {
        super( args, title, description, version, frameSetup );
        TestModule module = new TestModule();
        addModule( module );
    }
    
    public static void main( String[] args ) throws IOException {

        String title = "Test Location";
        String description = "Tests changes to semantics of PhetGraphic location";
        String version = "0.1";
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

        NonPiccoloPhetApplication app = new TestApplication( args, title, description, version, frameSetup );
        app.startApplication();
    }
}
