/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.fourier.module.ContinuousModule;
import edu.colorado.phet.fourier.module.DiscreteModule;
import edu.colorado.phet.fourier.module.DiscreteToContinousModule;


/**
 * FourierApplicationModel is the application model for the PhET
 * "Fourier Analysis" simulation.  It creates the clock and modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierApplicationModel extends ApplicationModel {

    private static final boolean TEST_ONE_MODULE = false;
    
    /**
     * Sole constructor.
     * 
     * @param title the application title
     * @param description the application description
     * @param version the application version
     * @param frameSetup info used to setup the application's frame
     */
    public FourierApplicationModel( String title, String description, String version, FrameSetup frameSetup ) {

        super( title, description, version, frameSetup );
        assert( frameSetup != null );

        // Clock
        boolean fixedDelay = true;
        AbstractClock clock = new SwingTimerClock( FourierConfig.TIME_STEP, FourierConfig.WAIT_TIME, fixedDelay );
        setClock( clock );

        // Clock control panel is disabled.
        setUseClockControlPanel( true );
        
        // Simulation Modules
        if ( TEST_ONE_MODULE ) {
            Module module = new DiscreteModule( clock );
            setModules( new Module[] { module } );
            setInitialModule( module );
        }
        else {
            DiscreteModule discreteModule = new DiscreteModule( clock );
            DiscreteToContinousModule discreteToContinuousModule = new DiscreteToContinousModule( clock );
            ContinuousModule continuousModule = new ContinuousModule( clock );
            setModules( new Module[] { discreteModule, discreteToContinuousModule, continuousModule } );
            setInitialModule( discreteModule );
        }
    }
}
