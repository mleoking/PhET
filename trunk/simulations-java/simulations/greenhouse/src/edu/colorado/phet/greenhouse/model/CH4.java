/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.greenhouse.GreenhouseConfig;


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

    private static final double HYDROGEN_VIBRATION_DISTANCE = 30;
    private static final double HYDROGEN_VIBRATION_ANGLE = Math.PI / 4;
    private static final double HYDROGEN_VIBRATION_DISTANCE_X = HYDROGEN_VIBRATION_DISTANCE * Math.cos( HYDROGEN_VIBRATION_ANGLE );
    private static final double HYDROGEN_VIBRATION_DISTANCE_Y = HYDROGEN_VIBRATION_DISTANCE * Math.sin( HYDROGEN_VIBRATION_ANGLE );

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
        setPhotonAbsorptionStrategy( GreenhouseConfig.irWavelength, new PhotonAbsorptionStrategy.VibrationStrategy( this ) );

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
        atomCogOffsets.put(carbonAtom, new Vector2D(0, 0));
        atomCogOffsets.put(hydrogenAtom1, new Vector2D(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom2, new Vector2D(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom3, new Vector2D(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom4, new Vector2D(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE,
                -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE));

        updateAtomPositions();
    }

    @Override
    protected void setVibration(double vibrationRadians){
        if (vibrationRadians != 0){
            double multFactor = Math.sin( vibrationRadians );
            atomCogOffsets.put(hydrogenAtom1,
                    new Vector2D(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_VIBRATION_DISTANCE_X,
                            ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_VIBRATION_DISTANCE_Y));
            atomCogOffsets.put(hydrogenAtom2,
                    new Vector2D(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE - multFactor * HYDROGEN_VIBRATION_DISTANCE_X,
                            ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_VIBRATION_DISTANCE_Y));
            atomCogOffsets.put(hydrogenAtom3,
                    new Vector2D(-ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE - multFactor * HYDROGEN_VIBRATION_DISTANCE_X,
                            -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_VIBRATION_DISTANCE_Y));
            atomCogOffsets.put(hydrogenAtom4,
                    new Vector2D(ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_VIBRATION_DISTANCE_X,
                            -ROTATED_INITIAL_CARBON_HYDROGEN_DISTANCE + multFactor * HYDROGEN_VIBRATION_DISTANCE_Y));

            // Position the carbon atom so that the center of mass of the
            // molecule remains the same.
            double carbonXPos = -(HydrogenAtom.MASS / CarbonAtom.MASS) *
                    (atomCogOffsets.get( hydrogenAtom1 ).getX() + atomCogOffsets.get( hydrogenAtom2 ).getX() +
                    atomCogOffsets.get( hydrogenAtom3 ).getX() + atomCogOffsets.get( hydrogenAtom4 ).getX());
            double carbonYPos = -(HydrogenAtom.MASS / CarbonAtom.MASS) *
                    (atomCogOffsets.get( hydrogenAtom1 ).getY() + atomCogOffsets.get( hydrogenAtom2 ).getY() +
                    atomCogOffsets.get( hydrogenAtom3 ).getY() + atomCogOffsets.get( hydrogenAtom4 ).getY());
            atomCogOffsets.put( carbonAtom, new Vector2D(carbonXPos, carbonYPos) );
        }
        else{
            initializeAtomOffsets();
        }
        updateAtomPositions();
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.CH4;
    }
}
