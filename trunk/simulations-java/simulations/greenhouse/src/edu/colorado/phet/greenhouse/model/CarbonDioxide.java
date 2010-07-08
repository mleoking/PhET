/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseConfig;


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
    
    private static final double OSCILLATION_FREQUENCY = 6;  // Cycles per second of sim time.
    
    private static final double PHOTON_ABSORPTION_DISTANCE = 200;
    
    private static final double MIN_PHOTON_HOLD_TIME = 1000; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 3000; // Milliseconds of sim time.
    private static final double ABSORPTION_HYSTERESIS_TIME = 200; // Milliseconds of sim time.
    
    private static final Random RAND = new Random();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond carbonOxygenBond1 = new AtomicBond( carbonAtom, oxygenAtom1, 2 );
    private final AtomicBond carbonOxygenBond2 = new AtomicBond( carbonAtom, oxygenAtom2, 2 );
    
    private double oscillationRadians = 0;
    
    private boolean photonAbsorbed = false;
    
    private double photonHoldCountdownTime = 0;
    private double absorbtionHysteresisCountdownTime = 0;

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
        
        if (photonAbsorbed){
            // A photon has been captured, so we should be oscillating.
            oscillationRadians += dt * OSCILLATION_FREQUENCY / 1000 * 2 * Math.PI;
            if (oscillationRadians >= 2 * Math.PI){
                oscillationRadians -= 2 * Math.PI;
            }
            // Move the carbon atom.
            carbonAtom.setPosition( carbonAtom.getPosition().getX(), 
                    carbonAtom.getPosition().getY() + OSC_DIST * Math.cos( oscillationRadians ) );
            oxygenAtom1.setPosition( oxygenAtom1.getPosition().getX(), 
                    oxygenAtom1.getPosition().getY() - OSC_DIST * Math.cos( oscillationRadians ) );
            oxygenAtom2.setPosition( oxygenAtom2.getPosition().getX(), 
                    oxygenAtom2.getPosition().getY() - OSC_DIST * Math.cos( oscillationRadians ) );
            
            // See if it is time to re-emit the photon.
            photonHoldCountdownTime -= dt;
            if (photonHoldCountdownTime <= 0){
                photonHoldCountdownTime = 0;
                emitPhoton( GreenhouseConfig.irWavelength );
                photonAbsorbed = false;
                absorbtionHysteresisCountdownTime = ABSORPTION_HYSTERESIS_TIME;
                setInitialConfiguration();
            }
        }
        
        if (absorbtionHysteresisCountdownTime > 0){
            absorbtionHysteresisCountdownTime -= dt;
        }
    }
    
    /**
     * Set or restore this molecule to its initial configuration, meaning that
     * no oscillatory bends are happening.
     */
    private void setInitialConfiguration(){
        Point2D cog = getCenterOfGravityPos();
        carbonAtom.setPosition( cog );
        oxygenAtom1.setPosition( cog.getX() - INITIAL_CARBON_OXYGEN_DISTANCE, cog.getY() );
        oxygenAtom2.setPosition( cog.getX() + INITIAL_CARBON_OXYGEN_DISTANCE, cog.getY() );
        oscillationRadians = 0;
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

    @Override
    public boolean absorbPhoton( Photon photon ) {
        if (!photonAbsorbed &&
             absorbtionHysteresisCountdownTime <= 0 &&
             photon.getWavelength() == GreenhouseConfig.irWavelength &&
             photon.getLocation().distance(carbonAtom.getPosition()) < PHOTON_ABSORPTION_DISTANCE)
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
