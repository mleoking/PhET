/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents carbon monoxide in the model.
 *
 * @author John Blanco
 */
public class CO extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final double INITIAL_CARBON_OXYGEN_DISTANCE = 170; // In picometers.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final AtomicBond carbonOxygenBond1 = new AtomicBond( carbonAtom, oxygenAtom1, 2 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public CO(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( carbonAtom );
        addAtom( oxygenAtom1 );
        addAtomicBond( carbonOxygenBond1 );

        // Set up the photon wavelengths to absorb.
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public CO(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------


    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.CO;
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        atomCogOffsets.put(carbonAtom, new Vector2D(-INITIAL_CARBON_OXYGEN_DISTANCE / 2, 0));
        atomCogOffsets.put(oxygenAtom1, new Vector2D(INITIAL_CARBON_OXYGEN_DISTANCE / 2, 0));
    }
}
