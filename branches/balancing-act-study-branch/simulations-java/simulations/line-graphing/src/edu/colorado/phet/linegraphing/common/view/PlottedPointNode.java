// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

/**
 * A plotted point on a graph, not interactive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlottedPointNode extends ShadedSphereNode {

    public PlottedPointNode( double diameter, Color fillColor ) {
        super( diameter, fillColor, Color.WHITE, fillColor.darker().darker(), false /* convertToImage */ );
        setStrokeAndPaint( new BasicStroke( 1f ), fillColor.darker().darker() );
        setPickable( false );
        setChildrenPickable( false );
    }
}
