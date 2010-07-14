/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents N2 (nitrogen) in the model.
 * 
 * @author John Blanco
 */
public class N2 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_NITROGEN_NITROGEN_DISTANCE = 170; // In picometers.
    
    private static final double OSCILLATION_FREQUENCY = 6;  // Cycles per second of sim time.
    
    private static final double PHOTON_ABSORPTION_DISTANCE = 200;
    
    private static final double MIN_PHOTON_HOLD_TIME = 500; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 1500; // Milliseconds of sim time.
    private static final double ABSORPTION_HYSTERESIS_TIME = 200; // Milliseconds of sim time.
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final NitrogenAtom nitrogenAtom1 = new NitrogenAtom();
    private final NitrogenAtom nitrogenAtom2 = new NitrogenAtom();
    private final AtomicBond nitrogenNitrogenBond = new AtomicBond( nitrogenAtom1, nitrogenAtom2, 1 );
    
    private double oscillationRadians = 0;
    
    private boolean photonAbsorbed = false;
    
    private double photonHoldCountdownTime = 0;
    private double absorbtionHysteresisCountdownTime = 0;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public N2(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( nitrogenAtom1 );
        addAtom( nitrogenAtom2 );
        addAtomicBond( nitrogenNitrogenBond );
        
        // Set the initial offsets.
        initializeCogOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public N2(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    @Override
    public void stepInTime( double dt ) {
        
        if (photonAbsorbed){
            // A photon has been captured, so we should be oscillating.
            oscillationRadians += dt * OSCILLATION_FREQUENCY / 1000 * 2 * Math.PI;
            if (oscillationRadians >= 2 * Math.PI){
                oscillationRadians -= 2 * Math.PI;
            }
            
            // See if it is time to re-emit the photon.
            photonHoldCountdownTime -= dt;
            if (photonHoldCountdownTime <= 0){
                photonHoldCountdownTime = 0;
                emitPhoton( GreenhouseConfig.irWavelength );
                photonAbsorbed = false;
                absorbtionHysteresisCountdownTime = ABSORPTION_HYSTERESIS_TIME;
                oscillationRadians = 0;
            }
            
            // Update the offset of the atoms based on the current oscillation
            // index.
            updateAtomOffsets();
            
            // Update the atom positions.
            updateAtomPositions();
        }
        
        if (absorbtionHysteresisCountdownTime > 0){
            absorbtionHysteresisCountdownTime -= dt;
        }
    }
    
    private void updateAtomOffsets(){
        // TODO
    }
    
    @Override
    public boolean absorbPhoton( Photon photon ) {
        if (!photonAbsorbed &&
             absorbtionHysteresisCountdownTime <= 0 &&
             photon.getWavelength() == GreenhouseConfig.irWavelength &&
             photon.getLocation().distance(nitrogenAtom2.getPosition()) < PHOTON_ABSORPTION_DISTANCE)
        {
            photonAbsorbed = true;
            photonHoldCountdownTime = MIN_PHOTON_HOLD_TIME + RAND.nextDouble() * (MAX_PHOTON_HOLD_TIME - MIN_PHOTON_HOLD_TIME);
            return true;
        }
        else{
            return false;
        }
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeCogOffsets() {
        atomCogOffsets.put(nitrogenAtom1, new PDimension(-INITIAL_NITROGEN_NITROGEN_DISTANCE / 2, 0));
        atomCogOffsets.put(nitrogenAtom2, new PDimension(INITIAL_NITROGEN_NITROGEN_DISTANCE / 2, 0));
    }
}
