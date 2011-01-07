// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DNAPivotNode is the visual representation of a DNAPivot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAPivotNode extends PPath {
    
    private static final double RADIUS = 2; // pixels
    private static final Color FILL_COLOR = Color.RED;
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );
    
    public DNAPivotNode( double xOffset, double yOffset ) {
        super();
        Shape pivotShape = new Ellipse2D.Double( xOffset - RADIUS, yOffset - RADIUS, 2 * RADIUS, 2 * RADIUS );
        setPathTo( pivotShape );
        setPaint( FILL_COLOR );
        setStroke( STROKE );
        setStrokePaint( STROKE_COLOR );
    }
}