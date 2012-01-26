// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.molarity;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.dilutions.MolarityResources.Strings;

/**
 * The "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityModule extends PiccoloModule {

    public MolarityModule( Frame frame ) {
        super( Strings.MOLARITY, new ConstantDtClock( 25 ) );
        setLogoPanel( null );
        setControlPanel( null );
        setClockControlPanel( null );
        setClockRunningWhenActive( false ); // no animation in this module
        setSimulationPanel( new MolarityCanvas( new MolarityModel(), frame ) );
    }
}