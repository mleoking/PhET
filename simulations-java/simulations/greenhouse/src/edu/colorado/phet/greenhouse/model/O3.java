/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


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
    private static final double INITIAL_MOLECULE_ANGLE = 104.5;
    private static final double INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET = 2 * OxygenAtom.MASS *
        OXYGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_MOLECULE_ANGLE ) / (OxygenAtom.MASS *
        2 * OxygenAtom.MASS);
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET -
        OXYGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_MOLECULE_ANGLE );
    private static final double INITIAL_OXYGEN_HORIZONTAL_OFFSET = OXYGEN_OXYGEN_BOND_LENGTH *
        Math.sin( INITIAL_MOLECULE_ANGLE );

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

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public O3(Point2D inititialCenterOfGravityPos){

        // Create the bond structure.  O3 has a type of bond where each O-O
        // has essentially 1.5 bonds, so we randomly choose one side to show
        // two bonds and another to show one.
        if ( RAND.nextBoolean() ){
            leftOxygenOxygenBond = new AtomicBond( centerOxygenAtom, leftOxygenAtom, 1 );
            rightOxygenOxygenBond = new AtomicBond( centerOxygenAtom, rightOxygenAtom, 2 );
        }
        else{
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
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public O3(){
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
        atomCogOffsets.put(centerOxygenAtom, new PDimension(0, INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(leftOxygenAtom, new PDimension(INITIAL_OXYGEN_HORIZONTAL_OFFSET,
                -INITIAL_OXYGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(rightOxygenAtom, new PDimension(-INITIAL_OXYGEN_HORIZONTAL_OFFSET,
                -INITIAL_OXYGEN_VERTICAL_OFFSET));
    }

    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        // TODO: This is temporary until we work out what the real oscillation
        // should look like.
        double multFactor = Math.sin( oscillationRadians );
        double maxHydrogenDisplacement = 20;
        double maxOxygenDisplacement = 5;
        atomCogOffsets.put(centerOxygenAtom, new PDimension(0, INITIAL_CENTER_OXYGEN_VERTICAL_OFFSET - multFactor * maxOxygenDisplacement));
        atomCogOffsets.put(leftOxygenAtom, new PDimension(INITIAL_OXYGEN_HORIZONTAL_OFFSET - multFactor * maxHydrogenDisplacement,
                -INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement));
        atomCogOffsets.put(rightOxygenAtom, new PDimension(-INITIAL_OXYGEN_HORIZONTAL_OFFSET + multFactor * maxHydrogenDisplacement,
                -INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement));
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.NO2;
    }
}
