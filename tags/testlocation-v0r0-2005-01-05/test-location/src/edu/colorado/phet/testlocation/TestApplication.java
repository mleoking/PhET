package edu.colorado.phet.testlocation;

import java.io.IOException;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;

/**
 * TestApplication is a test application that demonstrates proposed changes
 * to the semantics of PhetGraphic's location.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestApplication extends PhetApplication {

    public TestApplication() throws IOException {
        super( new LocationApplicationModel() );
    }
    
    private static class LocationApplicationModel extends ApplicationModel {
        public LocationApplicationModel()  {
            super( "Test Location", "Tests changes to semantics of PhetGraphic location", "0.1" );
            FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
            setFrameSetup( frameSetup );
            setClock( new SwingTimerClock( 1, 16 ) );
            TestModule module = new TestModule( this );
            setModules( new Module[] { module } );
            setInitialModule( module );
        }
    }
    
    public static void main( String[] args ) {
        try {
            PhetApplication app = new TestApplication();
            app.startApplication();
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit( 1 );
        }
    }
}
