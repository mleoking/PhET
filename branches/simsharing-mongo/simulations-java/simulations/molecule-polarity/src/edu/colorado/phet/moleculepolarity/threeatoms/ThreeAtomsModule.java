// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * "Three Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsModule extends SimSharingPiccoloModule {

    public ThreeAtomsModule( Frame parentFrame ) {
        super( UserComponents.threeAtomsTab, MPStrings.THREE_ATOMS, new MPClock() );

        ThreeAtomsModel model = new ThreeAtomsModel( getClock() );
        ViewProperties viewProperties = new ViewProperties( SurfaceType.NONE, false, true, false, false, false, false );
        setSimulationPanel( new ThreeAtomsCanvas( model, viewProperties, parentFrame ) );

        setClockControlPanel( null );
        setLogoPanel( null ); //WORKAROUND: if we don't do this, then scenegraph visibly resizes on Mac at startup, see #3092
    }
}
