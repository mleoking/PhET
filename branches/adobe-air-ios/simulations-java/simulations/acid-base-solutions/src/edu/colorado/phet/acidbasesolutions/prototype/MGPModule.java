// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPModule extends PiccoloModule {

    public MGPModule( JFrame parentFrame, boolean dev ) {
        super( "Magnifying Glass", new ConstantDtClock( 1000, 1 ), true /* startsPaused */);
        MGPModel model = new MGPModel();
        MGPCanvas canvas = new MGPCanvas( model, dev );
        setSimulationPanel( canvas );
        MGPControlPanel controlPanel = new MGPControlPanel( parentFrame, canvas, model, dev );
        setControlPanel( controlPanel );
        setClockControlPanel( null );
        setLogoPanel( null );
    }
}
