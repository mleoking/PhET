// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.manipulator;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

/**
 * Manipulator for interacting directly with a line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineManipulatorNode extends ShadedSphereNode {

    /**
     * Constructor.
     * @param diameter diameter of the sphere
     * @param baseColor base color use to shade the sphere
     */
    public LineManipulatorNode( double diameter, Color baseColor ) {
        super( diameter, baseColor, Color.WHITE, baseColor.darker().darker(), false /* convertToImage */ );
        setStrokeAndPaint( new BasicStroke( 1f ), baseColor.darker().darker() );
        addInputEventListener( new CursorHandler() ); // all manipulators are interactive
    }
}
