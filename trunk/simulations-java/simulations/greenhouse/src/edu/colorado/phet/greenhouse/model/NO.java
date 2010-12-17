/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;


/**
 * Class that represents NO (nitrogen monoxide) in the model.
 *
 * @author John Blanco
 */
public class NO extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final double INITIAL_NITROGEN_OXYGEN_DISTANCE = 170; // In picometers.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final NitrogenAtom nitrogenAtom = new NitrogenAtom();
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final AtomicBond nitrogenOxygenBond = new AtomicBond( nitrogenAtom, oxygenAtom, 2 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public NO(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( nitrogenAtom );
        addAtom( oxygenAtom );
        addAtomicBond( nitrogenOxygenBond );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public NO(){
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
        atomCogOffsets.put(nitrogenAtom, new Vector2D(-INITIAL_NITROGEN_OXYGEN_DISTANCE / 2, 0));
        atomCogOffsets.put(oxygenAtom, new Vector2D(INITIAL_NITROGEN_OXYGEN_DISTANCE / 2, 0));

        updateAtomPositions();
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.NO;
    }
}
