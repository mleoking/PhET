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
        boolean fixedDelay = true;
        this.setClock( new SwingTimerClock( FaradayConfig.TIME_STEP, FaradayConfig.WAIT_TIME, fixedDelay ) );

        // Clock control panel is disabled.
        setUseClockControlPanel( false );
        
        // Simulation Modules
        if ( TEST_ONE_MODULE ) {
            Module module = new ElectromagnetModule( this );
            this.setModules( new Module[] { module } );
            this.setInitialModule( module );
        }
        else {
            BarMagnetModule barMagnetModule = new BarMagnetModule( this );
            MagnetAndCoilModule magnetAndCoilModule = new MagnetAndCoilModule( this );
            ElectromagnetModule electromagnetModule = new ElectromagnetModule( this );
            TransformerModule transformerModule = new TransformerModule( this );
            GeneratorModule generatorModule = new GeneratorModule( this );
            this.setModules( new Module[] { barMagnetModule, magnetAndCoilModule, electromagnetModule, transformerModule, generatorModule } );
            this.setInitialModule( generatorModule );
        }
    }
}
