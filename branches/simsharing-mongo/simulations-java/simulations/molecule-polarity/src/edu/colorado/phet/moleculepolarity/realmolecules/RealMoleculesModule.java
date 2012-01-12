// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.realmolecules;

import java.awt.Frame;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.MPClock;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * "Real Molecules" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealMoleculesModule extends SimSharingPiccoloModule {

    private final RealMoleculesCanvas canvas;

    public RealMoleculesModule( Frame parentFrame ) {
        super( UserComponents.realMoleculesTab, MPStrings.REAL_MOLECULES, new MPClock() );

        RealMoleculesModel model = new RealMoleculesModel();
        ViewProperties viewProperties = new ViewProperties( SurfaceType.NONE, false, false, false, false, true, false );
        canvas = new RealMoleculesCanvas( model, viewProperties, parentFrame );
        setSimulationPanel( canvas );

        setClockControlPanel( null );
        setLogoPanel( null ); //WORKAROUND: if we don't do this, then scenegraph visibly resizes on Mac at startup, see #3092
    }

    public JmolViewer getJmolViewer() {
        return canvas.getJmolViewer();
    }
}
