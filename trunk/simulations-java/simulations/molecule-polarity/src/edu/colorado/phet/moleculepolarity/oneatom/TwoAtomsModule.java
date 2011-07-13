// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.oneatom;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;

/**
 * "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModule extends PiccoloModule {

    public TwoAtomsModule( Frame parentFrame ) {
        super( MPStrings.TWO_ATOMS, new MPClock() );
        TwoAtomsModel model = new TwoAtomsModel();
        ViewProperties viewProperties = new ViewProperties();
        setSimulationPanel( new TwoAtomsCanvas( model, viewProperties, parentFrame ) );
        setClockControlPanel( null );
    }
}
