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
    
    private static final double INITIAL_CARBON_OXYGEN_DISTANCE = 200; // In picometers.
    
    private static final double OSCILLATION_FREQUENCY = 4;  // Cycles per second of sim time.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond carbonOxygenBond1 = new AtomicBond( carbonAtom, oxygenAtom1, 2 );
    private final AtomicBond carbonOxygenBond2 = new AtomicBond( carbonAtom, oxygenAtom2, 2 );
    
    private double oscillationRadians = 0;

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
    
    private static final double OSC_DIST = 10;
    @Override
    public void stepInTime( double dt ) {
        oscillationRadians += dt * OSCILLATION_FREQUENCY / 1000 * 2 * Math.PI;
        if (oscillationRadians >= 2 * Math.PI){
            oscillationRadians -= 2 * Math.PI;
        }
        // Save the current center of gravity.
        Point2D centerOfGravityPos = getCenterOfGravityPos();
        // Move the carbon atom.
        carbonAtom.setPosition( carbonAtom.getPosition().getX(), carbonAtom.getPosition().getY() + OSC_DIST * Math.cos( oscillationRadians ) );
        oxygenAtom1.setPosition( oxygenAtom1.getPosition().getX(), oxygenAtom1.getPosition().getY() - OSC_DIST * Math.cos( oscillationRadians ) );
        oxygenAtom2.setPosition( oxygenAtom2.getPosition().getX(), oxygenAtom2.getPosition().getY() - OSC_DIST * Math.cos( oscillationRadians ) );
//        setCenterOfGravityPos( centerOfGravityPos );
    }

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
