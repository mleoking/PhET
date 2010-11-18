/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


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
    private static final double NITROGEN_OXYGEN_BOND_LENGTH = 150;
    private static final double INITIAL_OXYGEN_NITROGEN_ANGLE = 104.5;
    private static final double INITIAL_NITROGEN_VERTICAL_OFFSET = 2 * OxygenAtom.MASS *
        NITROGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_OXYGEN_NITROGEN_ANGLE ) / (NitrogenAtom.MASS *
        2 * HydrogenAtom.MASS);
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = INITIAL_NITROGEN_VERTICAL_OFFSET -
        NITROGEN_OXYGEN_BOND_LENGTH * Math.cos( INITIAL_OXYGEN_NITROGEN_ANGLE );
    private static final double INITIAL_OXYGEN_HORIZONTAL_OFFSET = NITROGEN_OXYGEN_BOND_LENGTH *
        Math.sin( INITIAL_OXYGEN_NITROGEN_ANGLE );

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final NitrogenAtom nitrogenAtom = new NitrogenAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond nitrogenOxygenBond1 = new AtomicBond( nitrogenAtom, oxygenAtom1, 1 );
    private final AtomicBond nitrogenOxygenBond2 = new AtomicBond( nitrogenAtom, oxygenAtom2, 1 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public NO2(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( nitrogenAtom );
        addAtom( oxygenAtom1 );
        addAtom( oxygenAtom2 );
        addAtomicBond( nitrogenOxygenBond1 );
        addAtomicBond( nitrogenOxygenBond2 );

        // Set up the photon wavelengths to absorb.
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );

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
        atomCogOffsets.put(nitrogenAtom, new PDimension(0, INITIAL_NITROGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(oxygenAtom1, new PDimension(INITIAL_OXYGEN_HORIZONTAL_OFFSET,
                -INITIAL_OXYGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(oxygenAtom2, new PDimension(-INITIAL_OXYGEN_HORIZONTAL_OFFSET,
                -INITIAL_OXYGEN_VERTICAL_OFFSET));
    }

    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        // TODO: This is temporary until we work out what the real oscillation
        // should look like.
        double multFactor = Math.sin( oscillationRadians );
        double maxHydrogenDisplacement = 20;
        double maxOxygenDisplacement = 5;
        atomCogOffsets.put(nitrogenAtom, new PDimension(0, INITIAL_NITROGEN_VERTICAL_OFFSET - multFactor * maxOxygenDisplacement));
        atomCogOffsets.put(oxygenAtom1, new PDimension(INITIAL_OXYGEN_HORIZONTAL_OFFSET - multFactor * maxHydrogenDisplacement,
                -INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement));
        atomCogOffsets.put(oxygenAtom2, new PDimension(-INITIAL_OXYGEN_HORIZONTAL_OFFSET + multFactor * maxHydrogenDisplacement,
                -INITIAL_OXYGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement));
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.NO2;
    }
}
