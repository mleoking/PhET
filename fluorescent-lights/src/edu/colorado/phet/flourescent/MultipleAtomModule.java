/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.model.clock.AbstractClock;

/**
 * MultipleAtomModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MultipleAtomModule extends DischargeLampModule {
//    private AtomicState[] atomicStates;

    /**
     * Constructor
     *
     * @param clock
     */
    protected MultipleAtomModule( String name, AbstractClock clock, int numAtoms, int numEnergyLevels, double maxAtomSpeed ) {
        super( name, clock, numEnergyLevels );
        addAtoms( getTube(), numAtoms, numEnergyLevels, maxAtomSpeed );
        addControls();
    }

    private void addControls() {
//        // Add an energy level monitor panel. Note that the panel has a null layout, so we have to put it in a
//        // panel that does have one, so it gets laid out properly
//        final DischargeLampEnergyMonitorPanel2 elmp = new DischargeLampEnergyMonitorPanel2( this, getClock(),
//                                                                                            atomicStates, 150, 300 );
//        getControlPanel().add( elmp );
//
    }
//
//    /**
//     * Adds some atoms and their graphics
//     *
//     * @param tube
//     * @param numAtoms
//     */
//    private void addAtoms( ResonatingCavity tube, int numAtoms, int numEnergyLevels, double maxSpeed ) {
//        DischargeLampAtom atom = null;
//        ArrayList atoms = new ArrayList();
//        Rectangle2D tubeBounds = tube.getBounds();
//
//        // Todo: consolidate for both modules
//        atomicStates = createAtomicStates( numEnergyLevels );
//
//        for( int i = 0; i < numAtoms; i++ ) {
//            atom = new DischargeLampAtom( (LaserModel)getModel(), atomicStates );
//            atom.setPosition( ( tubeBounds.getX() + ( Math.random() ) * ( tubeBounds.getWidth() - atom.getRadius() * 4 ) + atom.getRadius() * 2 ),
//                              ( tubeBounds.getY() + ( Math.random() ) * ( tubeBounds.getHeight() - atom.getRadius() * 4 ) ) + atom.getRadius() * 2 );
//            atom.setVelocity( (float)( Math.random() - 0.5 ) * maxSpeed,
//                              (float)( Math.random() - 0.5 ) * maxSpeed );
//            atoms.add( atom );
//            addAtom( atom );
//            atom.addPhotonEmittedListener( getSpectrometer() );
//        }
//    }
}
