/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.faraday.module.*;


/**
 * FaradayApplicationModel is the application model for the PhET
 * "Faraday's Law" simulation.  It creates the clock and modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayApplicationModel extends ApplicationModel {

    // Set this to true if you wish to test one module in isolation.
    private static final boolean TEST_ONE_MODULE = false;
    
    /**
     * Sole constructor.
     * 
     * @param title the application title
     * @param description the application description
     * @param version the application version
     * @param frameSetup info used to setup the application's frame
     */
    public FaradayApplicationModel( String title, String description, String version, FrameSetup frameSetup ) {

        super( title, description, version, frameSetup );
        assert( frameSetup != null );

        // Clock
        double timeStep = FaradayConfig.CLOCK_TIME_STEP;
        int waitTime = ( 1000 / FaradayConfig.CLOCK_FRAME_RATE ); // milliseconds
        boolean isFixed = FaradayConfig.CLOCK_TIME_STEP_IS_CONSTANT;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        setClock( clock );

        // Clock control panel.
        setUseClockControlPanel( FaradayConfig.CLOCK_ENABLE_CONTROLS );
        
        // Simulation Modules
        if ( TEST_ONE_MODULE ) {
            Module module = new ElectromagnetModule( clock );
            setModule( module );
        }
        else {
            BarMagnetModule barMagnetModule = new BarMagnetModule( clock );
            PickupCoilModule pickupCoilModule = new PickupCoilModule( clock );
            ElectromagnetModule electromagnetModule = new ElectromagnetModule( clock );
            TransformerModule transformerModule = new TransformerModule( clock );
            GeneratorModule generatorModule = new GeneratorModule( clock );
            setModules( new Module[] { barMagnetModule, pickupCoilModule, electromagnetModule, transformerModule, generatorModule } );
            setInitialModule( barMagnetModule );
        }
    }
}
