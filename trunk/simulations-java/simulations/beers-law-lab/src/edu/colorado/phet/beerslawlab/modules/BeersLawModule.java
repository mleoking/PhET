// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.modules;

import edu.colorado.phet.beerslawlab.BLLResources;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * The "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawModule extends PiccoloModule {

    public BeersLawModule() {
        super( BLLResources.Strings.BEERS_LAW, new ConstantDtClock( 25 ) );
        setLogoPanel( null );
        setControlPanel( null );
        setClockControlPanel( null );
        setSimulationPanel( new PhetPCanvas() );
    }
}
