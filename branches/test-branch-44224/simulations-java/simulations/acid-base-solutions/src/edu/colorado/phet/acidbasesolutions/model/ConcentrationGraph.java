/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of the concentration graph.
 * Exists purely for location and layout purposes.
 * Location is at the center of the graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationGraph extends SolutionRepresentation {
    
    private final PDimension size;
    
    public ConcentrationGraph( AqueousSolution solution, Point2D location, boolean visible, PDimension size ) {
        super( solution, location, visible );
        this.size = new PDimension( size );
    }
    
    public PDimension getSizeReference() {
        return size;
    }
    
    public double getWidth() {
        return size.getWidth();
    }
    
    public double getHeight() {
        return size.getHeight();
    }
}
