/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * Class that represents an atom of hydrogen in the model.
 * 
 * @author John Blanco
 */
public class HydrogenAtom extends Atom {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final Color REPRESENTATION_COLOR = Color.WHITE;
    public static final double MASS = 1;   // In atomic mass units (AMU).
    private static final double RADIUS = 37;     // In picometers.

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public HydrogenAtom( Point2D position ) {
        super( REPRESENTATION_COLOR, RADIUS, MASS, position );
    }
    
    public HydrogenAtom() {
        this( new Point2D.Double(0, 0) );
    }    
}
