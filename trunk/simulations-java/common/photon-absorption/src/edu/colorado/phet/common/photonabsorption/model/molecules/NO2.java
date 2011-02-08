// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.model.molecules;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.photonabsorption.model.Molecule;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.model.*;


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
    private static final double INITIAL_MOLECULE_HEIGHT = NITROGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE / 2 );
    private static final double TOTAL_MOLECULE_MASS = NitrogenAtom.MASS + ( 2 * OxygenAtom.MASS );
    private static final double INITIAL_NITROGEN_VERTICAL_OFFSET = INITIAL_MOLECULE_HEIGHT * ( ( 2 * OxygenAtom.MASS ) / TOTAL_MOLECULE_MASS );
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = -( INITIAL_MOLECULE_HEIGHT - INITIAL_NITROGEN_VERTICAL_OFFSET );
    private static final double INITIAL_OXYGEN_HORIZONTAL_OFFSET = NITROGEN_OXYGEN_BOND_LENGTH * Math.sin( INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE / 2 );

    // Random variable used to control the side on which the delocalized bond
    // is depicted.
    private static final Random RAND = new Random();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final NitrogenAtom nitrogenAtom = new NitrogenAtom();
    private final OxygenAtom rightOxygenAtom = new OxygenAtom();
    private final OxygenAtom leftOxygenAtom = new OxygenAtom();
    private final AtomicBond rightNitrogenOxygenBond;
    private final AtomicBond leftNitrogenOxygenBond;

    // Tracks the side on which the double bond is shown.  More on this where
    // it is initialized.
    private final boolean doubleBondOnRight;

    public NO2(Point2D inititialCenterOfGravityPos){

        // Create the bond structure.  NO2 has a type of bond where each N-O
        // has essentially 1.5 bonds, so we randomly choose one side to show
        // two bonds and another to show one.
        doubleBondOnRight = RAND.nextBoolean();
        if ( doubleBondOnRight ){
            rightNitrogenOxygenBond = new AtomicBond( nitrogenAtom, rightOxygenAtom, 2 );
            leftNitrogenOxygenBond = new AtomicBond( nitrogenAtom, leftOxygenAtom, 1 );
        }
        else{
            rightNitrogenOxygenBond = new AtomicBond( nitrogenAtom, rightOxygenAtom, 1 );
            leftNitrogenOxygenBond = new AtomicBond( nitrogenAtom, leftOxygenAtom, 2 );
        }

        // Add the atoms.
        addAtom( nitrogenAtom );
        addAtom( rightOxygenAtom );
        addAtom( leftOxygenAtom );
        addAtomicBond( rightNitrogenOxygenBond );
        addAtomicBond( leftNitrogenOxygenBond );

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
     * @see edu.colorado.phet.common.photonabsorption.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        initialAtomCogOffsets.put(nitrogenAtom, new Vector2D(0, INITIAL_NITROGEN_VERTICAL_OFFSET));
        initialAtomCogOffsets.put(rightOxygenAtom, new Vector2D(INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET));
        initialAtomCogOffsets.put(leftOxygenAtom, new Vector2D(-INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET));

        updateAtomPositions();
    }

    @Override
    public void setVibration( double vibrationRadians ) {
        super.setVibration( vibrationRadians );
        double multFactor = Math.sin( vibrationRadians );
        double maxNitrogenDisplacement = 30;
        double maxOxygenDisplacement = 15;
        initialAtomCogOffsets.put( nitrogenAtom, new Vector2D( 0, INITIAL_NITROGEN_VERTICAL_OFFSET - multFactor * maxNitrogenDisplacement ) );
        initialAtomCogOffsets.put( rightOxygenAtom, new Vector2D( INITIAL_OXYGEN_HORIZONTAL_OFFSET + multFactor * maxOxygenDisplacement,
                INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOxygenDisplacement ) );
        initialAtomCogOffsets.put( leftOxygenAtom, new Vector2D( -INITIAL_OXYGEN_HORIZONTAL_OFFSET - multFactor * maxOxygenDisplacement,
                INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxOxygenDisplacement ) );
        updateAtomPositions();
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.NO2;
    }

    @Override
    public void breakApart() {

        // Create the constituent molecules that result from breaking apart.
        Molecule nitrogenMonoxideMolecule = new NO();
        Molecule singleOxygenMolecule = new O();

        // Set up the direction and velocity of the constituent molecules.
        // These are set up mostly to look good, and their directions and
        // velocities have little if anything to do with any physical rules
        // of atomic dissociation.
        double diatomicMoleculeRotationAngle = ( ( Math.PI / 2 ) - ( INITIAL_OXYGEN_NITROGEN_OXYGEN_ANGLE / 2 ) );
        final double breakApartAngle;
        if ( doubleBondOnRight ){
            nitrogenMonoxideMolecule.rotate( -diatomicMoleculeRotationAngle );
            nitrogenMonoxideMolecule.setCenterOfGravityPos( ( initialAtomCogOffsets.get( nitrogenAtom ).getX() + initialAtomCogOffsets.get( rightOxygenAtom ).getX() ) / 2,
                    ( initialAtomCogOffsets.get( nitrogenAtom ).getY() + initialAtomCogOffsets.get( rightOxygenAtom ).getY() ) / 2);
            breakApartAngle = Math.PI / 4 + RAND.nextDouble() * Math.PI / 4;
            singleOxygenMolecule.setCenterOfGravityPos( -INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET );
        }
        else{
            nitrogenMonoxideMolecule.rotate( Math.PI + diatomicMoleculeRotationAngle );
            breakApartAngle = Math.PI / 2 + RAND.nextDouble() * Math.PI / 4;
            nitrogenMonoxideMolecule.setCenterOfGravityPos( ( initialAtomCogOffsets.get( nitrogenAtom ).getX() + initialAtomCogOffsets.get( leftOxygenAtom ).getX() ) / 2,
                    ( initialAtomCogOffsets.get( nitrogenAtom ).getY() + initialAtomCogOffsets.get( leftOxygenAtom ).getY() ) / 2);
            singleOxygenMolecule.setCenterOfGravityPos( INITIAL_OXYGEN_HORIZONTAL_OFFSET, INITIAL_OXYGEN_VERTICAL_OFFSET );
        }
        nitrogenMonoxideMolecule.setVelocity( BREAK_APART_VELOCITY * 0.33 * Math.cos(breakApartAngle), BREAK_APART_VELOCITY * 0.33 * Math.sin(breakApartAngle) );
        singleOxygenMolecule.setVelocity( -BREAK_APART_VELOCITY * 0.67 * Math.cos(breakApartAngle), -BREAK_APART_VELOCITY * 0.67 * Math.sin(breakApartAngle) );

        // Add these constituent molecules to the constituent list.
        constituentMolecules.add( nitrogenMonoxideMolecule );
        constituentMolecules.add( singleOxygenMolecule );

        // Send out notifications about this molecule breaking apart.
        ArrayList<Listener> copyOfListeners = new ArrayList<Listener>( listeners );
        for ( Listener listener : copyOfListeners ) {
            listener.brokeApart( this );
        }
    }
}
