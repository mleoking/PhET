// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import java.awt.*;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.quantum.model.Atom;

/**
 * LevelIcon
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

/**
 * An icon that shows a small version of an atom with its enrgy level halo and text
 */
public class LevelIcon extends CompositePhetGraphic {
    private AnnotatedAtomGraphic atomGraphic;
    private Atom atom;

    public LevelIcon( Component component, final Atom atom ) {
        super( component );
        this.atom = atom;
        atom.setRadius( 5 );
        atomGraphic = new LevelIconAnnotatedAtomGraphic( getComponent(), atom );
        addGraphic( atomGraphic );
        update();
    }

    public void update() {
        // Note that the AnnotatedAtomGraphic changes the size of the atom in the
        // model so that it will detect hits by photons on it's energy halo. We need
        // to reset the radius to its original value, or the atom grows in size
        atom.setRadius( 5 );
        atomGraphic.setRegistrationPoint( (int) atom.getRadius() / 2, 0 );
        atomGraphic.stateChanged( null );
    }

    public void updateEnergy( double newEnergy ) {
        atom.getCurrState().setEnergyLevel( newEnergy );
        update();
    }
}

