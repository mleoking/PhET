package com.pixelzoom.test;

import java.awt.Color;
import java.io.IOException;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.FrameSetup;

/**
 * TestPhet is the skeleton of a basic PhET simulation that uses phetgraphics.
 * Use this as the starting point for creating test simulations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPhetgraphics {

    private static final int CLOCK_FRAME_RATE = 25; // fps
    private static final int CLOCK_DELAY = (int)( 1000 / CLOCK_FRAME_RATE );
    private static final double CLOCK_STEP = 1;
    
    public static void main( String args[] ) throws IOException {
        TestPhetgraphics test = new TestPhetgraphics( args );
    }

    public TestPhetgraphics( String[] args ) throws IOException {
        
        String title = "TestPhet";
        String description = "description";
        String version = "0.0";
        boolean useClockControlPanel = false;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
        
        PhetApplication app = new PhetApplication( args, title, description, version, frameSetup );
        
        Module module = new TestModule();
        
        app.setModules( new Module[] { module } );
        
        app.startApplication();
    }

    private class TestModule extends PhetGraphicsModule {

        public TestModule() {
            super( "TestModule", new SwingClock( CLOCK_DELAY, CLOCK_STEP ) );

            // Model
            BaseModel model = new BaseModel();
            setModel( model );

            // Apparatus Panel
            ApparatusPanel2 apparatusPanel = new ApparatusPanel2( getClock() );
            apparatusPanel.setBackground( Color.WHITE );
            setApparatusPanel( apparatusPanel );
            
            // Control Panel
            final ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
        }
    }
}