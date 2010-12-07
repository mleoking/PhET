/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.greenhouse.GreenhouseConfig;


/**
 * Class that represents nitrogen dioxide (NO2) in the model.
 *
 * @author John Blanco
 */
public class NO2 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // These constants define the initial shape of the NO2 atom.  The angle
    // between the atoms is intended to be correct, and the bond is somewhat
    // longer than real life.  The algebraic calculations are intended to make
    // it so that the bond length and/or the angle could be changed and the
    // correct center of gravity will be maintained.
    private static final double NITROGEN_OXYGEN_BOND_LENGTH = 180;
    private static final double INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE = 120 * Math.PI / 180; // In radians.
    private static final double INITIAL_NITROGEN_VERTICAL_OFFSET = 2 * OxygenAtom.MASS *
        NITROGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE ) /
        ( NitrogenAtom.MASS * 2 * OxygenAtom.MASS );
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = INITIAL_NITROGEN_VERTICAL_OFFSET -
        NITROGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE );
    private static final double INITIAL_OXYGEN_HORIZONTAL_OFFSET = NITROGEN_OXYGEN_BOND_LENGTH *
        Math.sin( INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE );

    // Random variable used to control the side on which the delocalized bond
    // is depicted.
    private static final Random RAND = new Random();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final NitrogenAtom nitrogenAtom = new NitrogenAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond nitrogenOxygenBond1;
    private final AtomicBond nitrogenOxygenBond2;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public NO2(Point2D inititialCenterOfGravityPos){

        // Create the bond structure.  NO2 has a type of bond where each N-O
        // has essentially 1.5 bonds, so we randomly choose one side to show
        // two bonds and another to show one.
        if ( RAND.nextBoolean() ){
            nitrogenOxygenBond1 = new AtomicBond( nitrogenAtom, oxygenAtom1, 1 );
            nitrogenOxygenBond2 = new AtomicBond( nitrogenAtom, oxygenAtom2, 2 );
        }
        else{
            nitrogenOxygenBond1 = new AtomicBond( nitrogenAtom, oxygenAtom1, 2 );
            nitrogenOxygenBond2 = new AtomicBond( nitrogenAtom, oxygenAtom2, 1 );
        }

        // Add the atoms.
        addAtom( nitrogenAtom );
        addAtom( oxygenAtom1 );
        addAtom( oxygenAtom2 );
        addAtomicBond( nitrogenOxygenBond1 );
        addAtomicBond( nitrogenOxygenBond2 );

        // Set up the photon wavelengths to absorb.
        setPhotonAbsorptionStrategy( GreenhouseConfig.microWavelength, new PhotonAbsorptionStrategy.RotationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.irWavelength, new PhotonAbsorptionStrategy.VibrationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.visibleWaveLength, new PhotonAbsorptionStrategy.ExcitationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.uvWavelength, new PhotonAbsorptionStrategy.BreakApartStrategy( this ) );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public NO2(){
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
        atomCogOffsets.put(nitrogenAtom, new Vector2D(0, INITIAL_NITROGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(oxygenAtom1, new Vector2D(INITIAL_OXYGEN_HORIZONTAL_OFFSET,
                -INITIAL_OXYGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(oxygenAtom2, new Vector2D(-INITIAL_OXYGEN_HORIZONTAL_OFFSET,
                -INITIAL_OXYGEN_VERTICAL_OFFSET));
    }

    @Override
    protected void setVibration( double vibrationRadians ) {
        double multFactor = Math.sin( vibrationRadians );
        double maxNitrogenDisplacement = 5;
        double maxOxygenDisplacement = 20;
        atomCogOffsets.put( nitrogenAtom, new Vector2D( 0, INITIAL_NITROGEN_VERTICAL_OFFSET - multFactor * maxNitrogenDisplacement ) );
        atomCogOffsets.put( oxygenAtom1, new Vector2D( -INITIAL_OXYGEN_HORIZONTAL_OFFSET - multFactor * maxOxygenDisplacement,
                -INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOxygenDisplacement ) );
        atomCogOffsets.put( oxygenAtom2, new Vector2D( INITIAL_OXYGEN_HORIZONTAL_OFFSET + multFactor * maxOxygenDisplacement,
                -INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOxygenDisplacement ) );
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.NO2;
    }
}
