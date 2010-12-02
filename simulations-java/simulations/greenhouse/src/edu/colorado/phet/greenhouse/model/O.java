/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents a single atom of oxygen in the model.
 *
 * @author John Blanco
 */
public class O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final OxygenAtom oxygenAtom = new OxygenAtom();

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public O( Point2D inititialCenterOfGravityPos ) {

        addAtom( oxygenAtom );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public O() {
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
        atomCogOffsets.put( oxygenAtom, new Vector2D( 0, 0 ) );
    }

    @Override
    public MoleculeID getMoleculeID() {
        return MoleculeID.O;
    }
}
