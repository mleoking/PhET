// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsModule extends SimSharingPiccoloModule {

    public TwoAtomsModule( Frame parentFrame ) {
        super( UserComponents.twoAtomsTab, MPStrings.TWO_ATOMS, new MPClock() );

        TwoAtomsModel model = new TwoAtomsModel( getClock() );
        ViewProperties viewProperties = new ViewProperties( SurfaceType.NONE, true, false, false, false, false, false );
        setSimulationPanel( new TwoAtomsCanvas( model, viewProperties, parentFrame ) );

        setClockControlPanel( null );
        setLogoPanel( null ); //WORKAROUND: if we don't do this, then scenegraph visibly resizes on Mac at startup, see #3092
    }
}
