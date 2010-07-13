/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseConfig;


/**
 * Class that represents water (H2O) in the model.
 * 
 * @author John Blanco
 */
public class H2O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_OXYGEN_HYDROGEN_DISTANCE = 170; // In picometers.
    
    private static final double OSCILLATION_FREQUENCY = 6;  // Cycles per second of sim time.
    
    private static final double PHOTON_ABSORPTION_DISTANCE = 200;
    
    private static final double MIN_PHOTON_HOLD_TIME = 500; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 1500; // Milliseconds of sim time.
    private static final double ABSORPTION_HYSTERESIS_TIME = 200; // Milliseconds of sim time.
    
    private static final Random RAND = new Random();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final HydrogenAtom hydrogenAtom1 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom2 = new HydrogenAtom();
    private final AtomicBond oxygenHydrogenBond1 = new AtomicBond( oxygenAtom, hydrogenAtom1, 1 );
    private final AtomicBond oxygenHydrogenBond2 = new AtomicBond( oxygenAtom, hydrogenAtom2, 1 );
    
    private double oscillationRadians = 0;
    
    private boolean photonAbsorbed = false;
    
    private double photonHoldCountdownTime = 0;
    private double absorbtionHysteresisCountdownTime = 0;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public H2O(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( oxygenAtom );
        addAtom( hydrogenAtom1 );
        addAtom( hydrogenAtom2 );
        addAtomicBond( oxygenHydrogenBond1 );
        addAtomicBond( oxygenHydrogenBond2 );
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public H2O(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    /**
     * Set or restore this molecule to its initial configuration, meaning that
     * no oscillatory bends are happening.
     */
    private void setInitialConfiguration(){
        Point2D cog = getCenterOfGravityPos();
        // TODO: This is not right and needs work.
        oxygenAtom.setPosition( cog );
        hydrogenAtom1.setPosition( cog.getX() - INITIAL_OXYGEN_HYDROGEN_DISTANCE, cog.getY() + 20 );
        hydrogenAtom2.setPosition( cog.getX() + INITIAL_OXYGEN_HYDROGEN_DISTANCE, cog.getY() + 20 );
        oscillationRadians = 0;
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#setCenterOfGravityPos(java.awt.geom.Point2D)
     */
    @Override
    public void setCenterOfGravityPos( Point2D centerOfGravityPos ) {
        
        // TODO: Does not handle oscillation.
        oxygenAtom.setPosition( centerOfGravityPos );
        hydrogenAtom1.setPosition( centerOfGravityPos.getX() - INITIAL_OXYGEN_HYDROGEN_DISTANCE, centerOfGravityPos.getY() );
        hydrogenAtom2.setPosition( centerOfGravityPos.getX() + INITIAL_OXYGEN_HYDROGEN_DISTANCE, centerOfGravityPos.getY() );
    }

    @Override
    public boolean absorbPhoton( Photon photon ) {
        if (!photonAbsorbed &&
             absorbtionHysteresisCountdownTime <= 0 &&
             photon.getWavelength() == GreenhouseConfig.irWavelength &&
             photon.getLocation().distance(oxygenAtom.getPosition()) < PHOTON_ABSORPTION_DISTANCE)
        {
            photonAbsorbed = true;
            photonHoldCountdownTime = MIN_PHOTON_HOLD_TIME + RAND.nextDouble() * (MAX_PHOTON_HOLD_TIME - MIN_PHOTON_HOLD_TIME);
            return true;
        }
        else{
            return false;
        }
    }
}
