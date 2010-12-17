/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
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

    // These constants define the initial shape of the water atom.  The angle
    // between the atoms is intended to be correct, and the bond is somewhat
    // longer than real life.  The algebraic calculations are intended to make
    // it so that the bond length and/or the angle could be changed and the
    // correct center of gravity will be maintained.
    private static final double OXYGEN_HYDROGEN_BOND_LENGTH = 130;
    private static final double INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE = 109 * Math.PI / 180;
    private static final double INITIAL_MOLECULE_HEIGHT = OXYGEN_HYDROGEN_BOND_LENGTH * Math.cos( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE / 2 );
    private static final double TOTAL_MOLECULE_MASS = OxygenAtom.MASS + ( 2 * HydrogenAtom.MASS );
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = INITIAL_MOLECULE_HEIGHT * ( ( 2 * HydrogenAtom.MASS ) / TOTAL_MOLECULE_MASS );
    private static final double INITIAL_HYDROGEN_VERTICAL_OFFSET = -( INITIAL_MOLECULE_HEIGHT - INITIAL_OXYGEN_VERTICAL_OFFSET );
    private static final double INITIAL_HYDROGEN_HORIZONTAL_OFFSET = OXYGEN_HYDROGEN_BOND_LENGTH * Math.sin( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE / 2 );

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final HydrogenAtom hydrogenAtom1 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom2 = new HydrogenAtom();
    private final AtomicBond oxygenHydrogenBond1 = new AtomicBond( oxygenAtom, hydrogenAtom1, 1 );
    private final AtomicBond oxygenHydrogenBond2 = new AtomicBond( oxygenAtom, hydrogenAtom2, 1 );

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

        // Set up the photon wavelengths to absorb.
        setPhotonAbsorptionStrategy( GreenhouseConfig.microWavelength, new PhotonAbsorptionStrategy.RotationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.irWavelength, new PhotonAbsorptionStrategy.VibrationStrategy( this ) );

        // Set the initial offsets.
        initializeAtomOffsets();

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
    protected void initializeAtomOffsets() {
        atomCogOffsets.put(oxygenAtom, new Vector2D(0, INITIAL_OXYGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(hydrogenAtom1, new Vector2D(INITIAL_HYDROGEN_HORIZONTAL_OFFSET, INITIAL_HYDROGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(hydrogenAtom2, new Vector2D(-INITIAL_HYDROGEN_HORIZONTAL_OFFSET, INITIAL_HYDROGEN_VERTICAL_OFFSET));

        updateAtomPositions();
    }

    @Override
    protected void setVibration( double vibrationRadians ) {
        super.setVibration( vibrationRadians );
        double multFactor = Math.sin( vibrationRadians );
        double maxOxygenDisplacement = 3;
        double maxHydrogenDisplacement = 18;
        atomCogOffsets.put( oxygenAtom, new Vector2D( 0, INITIAL_OXYGEN_VERTICAL_OFFSET - multFactor * maxOxygenDisplacement ) );
        atomCogOffsets.put( hydrogenAtom1, new Vector2D( INITIAL_HYDROGEN_HORIZONTAL_OFFSET + multFactor * maxHydrogenDisplacement,
                INITIAL_HYDROGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement ) );
        atomCogOffsets.put( hydrogenAtom2, new Vector2D( -INITIAL_HYDROGEN_HORIZONTAL_OFFSET - multFactor * maxHydrogenDisplacement,
                INITIAL_HYDROGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement ) );
        updateAtomPositions();
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.H2O;
    }
}
