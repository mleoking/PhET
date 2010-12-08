/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.greenhouse.GreenhouseConfig;


/**
 * Class that represents ozone (O3) in the model.
 *
 * @author John Blanco
 */
public class O3 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // These constants define the initial shape of the O3 atom.  The angle
    // between the atoms is intended to be correct, and the bond is somewhat
    // longer than real life.  The algebraic calculations are intended to make
    // it so that the bond length and/or the angle could be changed and the
    // correct center of gravity will be maintained.
    private static final double OXYGEN_OXYGEN_BOND_LENGTH = 180;
    private static final double INITIAL_OXYGEN_OXYGEN_OXYGEN_ANGLE = 120 * Math.PI / 180; // In radians.
    private static final double INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET = 2.0 / 3.0 * Math.cos( INITIAL_OXYGEN_OXYGEN_OXYGEN_ANGLE / 2 ) * OXYGEN_OXYGEN_BOND_LENGTH;
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = -INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET / 2;
    private static final double INITIAL_OXYGEN_HORIZONTAL_OFFSET = OXYGEN_OXYGEN_BOND_LENGTH * Math.sin( INITIAL_OXYGEN_OXYGEN_OXYGEN_ANGLE / 2 );

    // Scaler quantity representing the speed at which the constituent particles
    // move away from each other.  Note that this is a relative speed, not one
    // that is absolute in model space.
    private static final double BREAK_APART_VELOCITY = 3;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final OxygenAtom centerOxygenAtom = new OxygenAtom();
    private final OxygenAtom leftOxygenAtom = new OxygenAtom();
    private final OxygenAtom rightOxygenAtom = new OxygenAtom();
    private final AtomicBond leftOxygenOxygenBond;
    private final AtomicBond rightOxygenOxygenBond;

    // Random variable used to control the side on which the delocalized bond
    // is depicted.
    private static final Random RAND = new Random();

    // Constituent molecules - these come into play when the original molecule
    // is commanded to break apart.
    private final ArrayList<Molecule> consituentMolecules = new ArrayList<Molecule>();

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public O3( Point2D inititialCenterOfGravityPos ) {

        // Create the bond structure.  O3 has a type of bond where each O-O
        // has essentially 1.5 bonds, so we randomly choose one side to show
        // two bonds and another to show one.
        if ( RAND.nextBoolean() ) {
            leftOxygenOxygenBond = new AtomicBond( centerOxygenAtom, leftOxygenAtom, 1 );
            rightOxygenOxygenBond = new AtomicBond( centerOxygenAtom, rightOxygenAtom, 2 );
        }
        else {
            leftOxygenOxygenBond = new AtomicBond( centerOxygenAtom, leftOxygenAtom, 2 );
            rightOxygenOxygenBond = new AtomicBond( centerOxygenAtom, rightOxygenAtom, 1 );
        }

        // Add the atoms.
        addAtom( centerOxygenAtom );
        addAtom( leftOxygenAtom );
        addAtom( rightOxygenAtom );
        addAtomicBond( leftOxygenOxygenBond );
        addAtomicBond( rightOxygenOxygenBond );

        // Set up the photon wavelengths to absorb.
        setPhotonAbsorptionStrategy( GreenhouseConfig.microWavelength, new PhotonAbsorptionStrategy.RotationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.irWavelength, new PhotonAbsorptionStrategy.VibrationStrategy( this ) );
        setPhotonAbsorptionStrategy( GreenhouseConfig.uvWavelength, new PhotonAbsorptionStrategy.BreakApartStrategy( this ) );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public O3() {
        this( new Point2D.Double( 0, 0 ) );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        atomCogOffsets.put( centerOxygenAtom, new Vector2D( 0, INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET ) );
        atomCogOffsets.put( leftOxygenAtom, new Vector2D( INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET ) );
        atomCogOffsets.put( rightOxygenAtom, new Vector2D( -INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET ) );
    }

    @Override
    protected void setVibration( double vibrationRadians ) {
        double multFactor = Math.sin( vibrationRadians );
        double maxCenterOxygenDisplacement = 30;
        double maxOuterOxygenDisplacement = 15;
        atomCogOffsets.put( centerOxygenAtom, new Vector2D( 0, INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET - multFactor * maxCenterOxygenDisplacement ) );
        atomCogOffsets.put( leftOxygenAtom, new Vector2D( INITIAL_OXYGEN_HORIZONTAL_OFFSET + multFactor * maxOuterOxygenDisplacement,
                INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOuterOxygenDisplacement ) );
        atomCogOffsets.put( rightOxygenAtom, new Vector2D( -INITIAL_OXYGEN_HORIZONTAL_OFFSET - multFactor * maxOuterOxygenDisplacement,
                INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOuterOxygenDisplacement ) );
    }

    @Override
    public ArrayList<Molecule> getBreakApartConstituents() {
        return consituentMolecules;
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.O3;
    }

    @Override
    protected void breakApart() {

        // Choose the direction that the molecule will break apart, and
        // constrain it to be out of the plane of the photon motion.  This is
        // done solely to make sure that the constituents can be clearly seen,
        // and not for any physical reason.
        final double breakApartAngle = Math.PI / 2 + (RAND.nextDouble() - 0.5) * Math.PI;


        // Create the constituent molecules that result from breaking apart.
        Molecule diatomicOxygenMolecule = new O2(){{
            setVelocity( BREAK_APART_VELOCITY * 0.33 * Math.cos(breakApartAngle), BREAK_APART_VELOCITY * 0.33 * Math.cos(breakApartAngle) );
        }};
        Molecule singleOxygenMolecule = new O(){{
            setVelocity( -BREAK_APART_VELOCITY * 0.67 * Math.cos(breakApartAngle), -BREAK_APART_VELOCITY * 0.67 * Math.cos(breakApartAngle) );
        }};
        consituentMolecules.add( diatomicOxygenMolecule );
        consituentMolecules.add( singleOxygenMolecule );

        for ( Listener listener : listeners ) {
            listener.brokeApart(this );
        }
    }
}
