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
import edu.colorado.phet.lasers.model.atom.Atom;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
public class LevelIcon extends CompositePhetGraphic implements ChangeListener {
    private AnnotatedAtomGraphic atomGraphic;
    private Atom atom;

    public LevelIcon( Component component, final Atom atom ) {
        super( component );
        this.atom = atom;
        atom.setRadius( 5 );
        // Create a fixed currAtomState for the atom to be in
//        AtomicState currAtomState = new AtomicState( atom.getCurrState() );
//        atom.setCurrState( currAtomState );

//        atom.getCurrState().setMeanLifetime( Double.MAX_VALUE );
//        AtomicState currAtomState = atom.getCurrState();
//        currAtomState.addListener( new AtomicState.ChangeListenerAdapter() {
//            public void energyLevelChanged( AtomicState.Event event ) {
//                AtomicState currAtomState = new AtomicState( atom.getCurrState() );
//                atom.setCurrState( currAtomState );
//                atom.getCurrState().setMeanLifetime( Double.MAX_VALUE );
//                update();
//            }
//        } );
        update();
    }

    public void update() {
        if( atomGraphic != null ) {
            removeGraphic( atomGraphic );
        }
        atomGraphic = new AnnotatedAtomGraphic( getComponent(), atom );
        atomGraphic.setRegistrationPoint( (int)atom.getRadius() / 2, 0 );
        // Remove the graphic as a change listener, so it doesn't change it's state if
        // the dummy atom changes state becauses its lifetime expires
        atom.removeChangeListener( atomGraphic );
        addGraphic( atomGraphic );
    }

    //----------------------------------------------------------------
    // ChangeListener implementation
    //----------------------------------------------------------------
    public void stateChanged( ChangeEvent e ) {
        if( e.getSource() instanceof EnergyLevelGraphic ) {
            update();
        }
    }
}

