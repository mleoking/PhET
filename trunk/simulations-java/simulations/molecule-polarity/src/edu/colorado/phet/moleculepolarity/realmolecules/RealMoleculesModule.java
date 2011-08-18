// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModule extends PiccoloModule {

    public RealMoleculesModule( Frame parentFrame ) {
        super( MPStrings.REAL_MOLECULES, new MPClock() );
        RealMoleculesModel model = new RealMoleculesModel();
        ViewProperties viewProperties = new ViewProperties( SurfaceType.NONE, false, false, false, false, true, true );
        setSimulationPanel( new RealMoleculesCanvas( model, viewProperties, parentFrame ) );
        setClockControlPanel( null );
    }
}
