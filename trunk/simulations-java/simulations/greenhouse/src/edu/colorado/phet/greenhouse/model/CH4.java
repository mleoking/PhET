/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents CH4 (methane) in the model.
 * 
 * @author John Blanco
 */
public class CH4 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_CARBON_HYDROGEN_DISTANCE = 170; // In picometers.
    
    // Assume that the angle from the carbon to the hydrogen is 45 degrees.
    private static final double ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE =
        INITIAL_CARBON_HYDROGEN_DISTANCE * Math.sin( Math.PI / 4  );
    
    private static final double HYDROGEN_OSCILLATION_DISTANCE = 30;
    private static final double HYDROGEN_OSCILLATION_ANGLE = Math.PI / 4;
    private static final double HYDROGEN_OSCILLATION_DISTANCE_X = HYDROGEN_OSCILLATION_DISTANCE * Math.cos( HYDROGEN_OSCILLATION_ANGLE );
    private static final double HYDROGEN_OSCILLATION_DISTANCE_Y = HYDROGEN_OSCILLATION_DISTANCE * Math.sin( HYDROGEN_OSCILLATION_ANGLE );
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final HydrogenAtom hydrogenAtom1 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom2 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom3 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom4 = new HydrogenAtom();
    private final AtomicBond carbonHydrogenBond1 = new AtomicBond( carbonAtom, hydrogenAtom1, 1 );
    private final AtomicBond carbonHydrogenBond2 = new AtomicBond( carbonAtom, hydrogenAtom2, 1 );
    private final AtomicBond carbonHydrogenBond3 = new AtomicBond( carbonAtom, hydrogenAtom3, 1 );
    private final AtomicBond carbonHydrogenBond4 = new AtomicBond( carbonAtom, hydrogenAtom4, 1 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public CH4(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( carbonAtom );
        addAtom( hydrogenAtom1 );
        addAtom( hydrogenAtom2 );
        addAtom( hydrogenAtom3 );
        addAtom( hydrogenAtom4 );
        addAtomicBond( carbonHydrogenBond1 );
        addAtomicBond( carbonHydrogenBond2 );
        addAtomicBond( carbonHydrogenBond3 );
        addAtomicBond( carbonHydrogenBond4 );
        
        // Set up the photon wavelengths to absorb.
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public CH4(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        atomCogOffsets.put(carbonAtom, new PDimension(0, 0));
        atomCogOffsets.put(hydrogenAtom1, new PDimension(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom2, new PDimension(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom3, new PDimension(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom4, new PDimension(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
    }

    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        // TODO: This is temporary until we get the real vibration mode worked out.
        if (oscillationRadians != 0){
            double multFactor = Math.sin( oscillationRadians );
            atomCogOffsets.put(hydrogenAtom1, 
                    new PDimension(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_OSCILLATION_DISTANCE_X,
                            ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_OSCILLATION_DISTANCE_Y));
            atomCogOffsets.put(hydrogenAtom2, 
                    new PDimension(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE - multFactor * HYDROGEN_OSCILLATION_DISTANCE_X,
                            ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_OSCILLATION_DISTANCE_Y));
            atomCogOffsets.put(hydrogenAtom3, 
                    new PDimension(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE - multFactor * HYDROGEN_OSCILLATION_DISTANCE_X,
                            -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_OSCILLATION_DISTANCE_Y));
            atomCogOffsets.put(hydrogenAtom4, 
                    new PDimension(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_OSCILLATION_DISTANCE_X,
                            -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_OSCILLATION_DISTANCE_Y));
            // Position the carbon atom so that the center of mass remains
            // the same.
            
            // TODO: this isn't right yet.
            double carbonXPos = -(HydrogenAtom.MASS / CarbonAtom.MASS) * (hydrogenAtom1.getPosition().getX() +
                    hydrogenAtom2.getPosition().getX() + hydrogenAtom3.getPosition().getX() + 
                    hydrogenAtom4.getPosition().getX());
            double carbonYPos = -(HydrogenAtom.MASS / CarbonAtom.MASS) * (hydrogenAtom1.getPosition().getY() +
                    hydrogenAtom2.getPosition().getY() + hydrogenAtom3.getPosition().getY() + 
                    hydrogenAtom4.getPosition().getY());
            atomCogOffsets.put( carbonAtom, new PDimension(carbonXPos, carbonYPos) );
        }
        else{
            initializeAtomOffsets();
        }
    }
}
