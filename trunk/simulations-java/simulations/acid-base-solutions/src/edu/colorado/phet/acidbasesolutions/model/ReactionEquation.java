/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

/**
 * Model of reaction equation.
 * Exists purely for location and layout purposes.
 * Location is at the top center of the equation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactionEquation extends SolutionRepresentation {
    
    public ReactionEquation( AqueousSolution solution, Point2D location, boolean visible ) {
        super( solution, location, visible );
    }
}
