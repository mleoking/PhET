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
    private static final double INITIAL_MOLECULE_HEIGHT = OXYGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_OXYGEN_OXYGEN_OXYGEN_ANGLE / 2 );
    private static final double INITIAL_MOLECULE_WIDTH = 2 * OXYGEN_OXYGEN_BOND_LENGTH * Math.sin( INITIAL_OXYGEN_OXYGEN_OXYGEN_ANGLE / 2 );
    private static final double INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET = 2.0 / 3.0 * INITIAL_MOLECULE_HEIGHT;
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = -INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET / 2;
    private static final double INITIAL_OXYGEN_HORIZONTAL_OFFSET = INITIAL_MOLECULE_WIDTH / 2;

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

    // Tracks the side on which the double bond is shown.  More on this where
    // it is initialized.
    private final boolean doubleBondOnRight;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public O3( Point2D inititialCenterOfGravityPos ) {

        // Create the bond structure.  O3 has a type of bond where each O-O
        // has essentially 1.5 bonds, so we randomly choose one side to show
        // two bonds and another to show one.
        doubleBondOnRight = RAND.nextBoolean();
        if ( doubleBondOnRight ) {
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
        atomCogOffsets.put( leftOxygenAtom, new Vector2D( -INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET ) );
        atomCogOffsets.put( rightOxygenAtom, new Vector2D( INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET ) );

        updateAtomPositions();
    }

    @Override
    protected void setVibration( double vibrationRadians ) {
        double multFactor = Math.sin( vibrationRadians );
        double maxCenterOxygenDisplacement = 30;
        double maxOuterOxygenDisplacement = 15;
        atomCogOffsets.put( centerOxygenAtom, new Vector2D( 0, INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET - multFactor * maxCenterOxygenDisplacement ) );
        atomCogOffsets.put( rightOxygenAtom, new Vector2D( INITIAL_OXYGEN_HORIZONTAL_OFFSET + multFactor * maxOuterOxygenDisplacement,
                INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOuterOxygenDisplacement ) );
        atomCogOffsets.put( leftOxygenAtom, new Vector2D( -INITIAL_OXYGEN_HORIZONTAL_OFFSET - multFactor * maxOuterOxygenDisplacement,
                INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOuterOxygenDisplacement ) );
        updateAtomPositions();
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
        final double breakApartAngle;
        if ( doubleBondOnRight ){
            breakApartAngle = Math.PI / 4 + RAND.nextDouble() * Math.PI / 4;
        }
        else{
            breakApartAngle = Math.PI / 2 + RAND.nextDouble() * Math.PI / 4;
        }

        // Create the constituent molecules that result from breaking apart.
        Molecule diatomicOxygenMolecule = new O2();
        diatomicOxygenMolecule.setVelocity( BREAK_APART_VELOCITY * 0.33 * Math.cos(breakApartAngle), BREAK_APART_VELOCITY * 0.33 * Math.sin(breakApartAngle) );
        // Rotate the molecule so that its orientation matches that of the
        // side of the molecule where the double bond existed.  In other
        // words, the break appears where the single bond was.
        double rotationAngle = ( ( Math.PI / 2 ) - ( INITIAL_OXYGEN_OXYGEN_OXYGEN_ANGLE / 2 ) );
        if ( doubleBondOnRight ){
//            setCenterOfGravityPos( ( atomCogOffsets.get( rightOxygenAtom ).getX() + atomCogOffsets.get( centerOxygenAtom ).getX() ) / 2,
//                    ( atomCogOffsets.get( centerOxygenAtom ).getY() + atomCogOffsets.get( rightOxygenAtom ).getY() ) / 2);
            setCenterOfGravityPos( INITIAL_MOLECULE_WIDTH / 2, INITIAL_MOLECULE_HEIGHT / 2 );
            diatomicOxygenMolecule.rotate( -rotationAngle );
        }
        else{
            diatomicOxygenMolecule.rotate( rotationAngle );
            setCenterOfGravityPos( -500, INITIAL_OXYGEN_VERTICAL_OFFSET );
        }

        Molecule singleOxygenMolecule = new O(){{
            setVelocity( -BREAK_APART_VELOCITY * 0.67 * Math.cos(breakApartAngle), -BREAK_APART_VELOCITY * 0.67 * Math.sin(breakApartAngle) );
            if ( doubleBondOnRight ){
                setCenterOfGravityPos( -INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET );
            }
            else{
                setCenterOfGravityPos( INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET );
            }
        }};
        constituentMolecules.add( diatomicOxygenMolecule );
        constituentMolecules.add( singleOxygenMolecule );

        ArrayList<Listener> copyOfListeners = new ArrayList<Listener>( listeners );
        for ( Listener listener : copyOfListeners ) {
            listener.brokeApart( this );
        }
    }
}
