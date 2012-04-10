// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;

/**
 * Manipulator for interacting directly with a line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineManipulatorNode extends ShadedSphereNode {

    public LineManipulatorNode( double diameter, Color fillColor ) {
        super( diameter, fillColor, Color.WHITE, fillColor.darker().darker(), false /* convertToImage */ );
        setStrokeAndPaint( new BasicStroke( 1f ), fillColor.darker().darker() );
    }
}
