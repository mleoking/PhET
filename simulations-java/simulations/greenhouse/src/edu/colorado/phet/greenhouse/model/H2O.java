/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents water (H2O) in the model.
 * 
 * @author John Blanco
 */
public class H2O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    // TODO: These values should be calculated for correct COG.
    private static final Dimension2D INITIAL_OXYGEN_OFFSET = new PDimension(0, 10); // In picometers.
    private static final Dimension2D INITIAL_HYDROGEN1_OFFSET = new PDimension(-90, -90); // In picometers.
    private static final Dimension2D INITIAL_HYDROGEN2_OFFSET = new PDimension(90, -90); // In picometers.
    
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

        // Set the initial offsets.
        initializeCogOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public H2O(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeCogOffsets() {
        atomCogOffsets.put(oxygenAtom, INITIAL_OXYGEN_OFFSET);
        atomCogOffsets.put(hydrogenAtom1, INITIAL_HYDROGEN1_OFFSET);
        atomCogOffsets.put(hydrogenAtom2, INITIAL_HYDROGEN2_OFFSET);
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
