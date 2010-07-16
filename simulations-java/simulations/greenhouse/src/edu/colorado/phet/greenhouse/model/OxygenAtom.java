/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * Class that represents an atom of oxygen in the model.
 * 
 * @author John Blanco
 */
public class OxygenAtom extends Atom {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final Color REPRESENTATION_COLOR = Color.RED;
    public static final double MASS = 12.011;   // In atomic mass units (AMU).
    private static final double RADIUS = 73;     // In picometers.

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public OxygenAtom( Point2D position ) {
        super( REPRESENTATION_COLOR, RADIUS, MASS, position );
    }
    
    public OxygenAtom() {
        this( new Point2D.Double(0, 0) );
    }    
}
