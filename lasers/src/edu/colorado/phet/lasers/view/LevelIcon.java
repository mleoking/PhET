/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.view.AnnotatedAtomGraphic;

import java.awt.*;

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
        update();
    }

    public void update() {
        if( atomGraphic != null ) {
            removeGraphic( atomGraphic );
        }
        atomGraphic = new AnnotatedAtomGraphic( getComponent(), atom );

        // Note that the AnnotatedAtomGraphic changes the size of the atom in the
        // model so that it will detect hits by photons on it's energy halo. We need
        // to reset the radius to its original value, or the atom grows in size
        atom.setRadius( 5 );
        atomGraphic.setRegistrationPoint( (int)atom.getRadius() / 2, 0 );
        addGraphic( atomGraphic );


    }

    public void updateEnergy( double newEnergy ) {
        atom.getCurrState().setEnergyLevel( newEnergy );
        update();
    }
}

