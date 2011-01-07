/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * Class that represents an atom of nitrogen in the model.
 *
 * @author John Blanco
 */
public class NitrogenAtom extends Atom {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final Color REPRESENTATION_COLOR = Color.BLUE;
    public static final double MASS = 14.00674;   // In atomic mass units (AMU).
    private static final double RADIUS = 75;       // In picometers.

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public NitrogenAtom( Point2D position ) {
        super( REPRESENTATION_COLOR, RADIUS, MASS, position );
    }

    public NitrogenAtom() {
        this( new Point2D.Double(0, 0) );
    }
}
