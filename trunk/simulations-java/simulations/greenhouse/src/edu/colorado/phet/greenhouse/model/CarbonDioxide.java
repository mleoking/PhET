/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;


/**
 * Class that represents carbon dioxide in the model.
 * 
 * @author John Blanco
 */
public class CarbonDioxide extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_CARBON_OXYGEN_DISTANCE = 100; // In picometers.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond carbonOxygenBond1 = new AtomicBond( carbonAtom, oxygenAtom1 );
    private final AtomicBond carbonOxygenBond2 = new AtomicBond( carbonAtom, oxygenAtom2 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public CarbonDioxide(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( carbonAtom );
        addAtom( oxygenAtom1 );
        addAtom( oxygenAtom2 );
        addAtomicBond( carbonOxygenBond1 );
        addAtomicBond( carbonOxygenBond2 );
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#setCenterOfGravityPos(java.awt.geom.Point2D)
     */
    @Override
    public void setCenterOfGravityPos( Point2D centerOfGravityPos ) {
        
        // TODO: Does not handle oscillation.
        carbonAtom.setPosition( centerOfGravityPos );
        oxygenAtom1.setPosition( centerOfGravityPos.getX() - INITIAL_CARBON_OXYGEN_DISTANCE, centerOfGravityPos.getY() );
        oxygenAtom2.setPosition( centerOfGravityPos.getX() + INITIAL_CARBON_OXYGEN_DISTANCE, centerOfGravityPos.getY() );
    }
}
