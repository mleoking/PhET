// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.modules;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * The "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModule extends PiccoloModule {

    public ConcentrationModule() {
        super( Strings.CONCENTRATION, new ConstantDtClock( 25 ) );
        setLogoPanel( null );
        setControlPanel( null );
        setClockControlPanel( null );
        setSimulationPanel( new PhetPCanvas() );
    }
}
