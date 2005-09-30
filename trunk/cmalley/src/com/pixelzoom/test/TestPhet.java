package com.pixelzoom.test;

import java.awt.Color;
import java.io.IOException;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.FrameSetup;

/**
 * TestPhet is the skeleton of a basic PhET simulation.
 * Use this as the starting point for creating test simulations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPhet {

    public static void main( String args[] ) throws IOException {
        TestPhet test = new TestPhet( args );
    }

    public TestPhet( String[] args ) throws IOException {

        // Clock
        double timeStep = 1;
        double frameRate = 25; // fps
        int waitTime = (int)( 1000 / frameRate ); // milliseconds
        boolean isFixed = true;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        
        String title = "TestPhet";
        String description = "description";
        String version = "0.0";
        boolean useClockControlPanel = false;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
        
        PhetApplication app = new PhetApplication( args,
                title, description, version, clock, useClockControlPanel, frameSetup );
        
        Module module = new TestModule( clock );
        
        app.setModules( new Module[] { module } );
        
        app.startApplication();
    }

    private class TestModule extends Module {

        public TestModule( AbstractClock clock ) {
            super( "TestModule", clock );

            // Model
            BaseModel model = new BaseModel();
            setModel( model );

            // Apparatus Panel
            ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
            apparatusPanel.setBackground( Color.WHITE );
            setApparatusPanel( apparatusPanel );
            
            // Control Panel
            final ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
        }
    }
}