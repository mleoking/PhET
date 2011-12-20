// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Single slice for a pie.
 */
public class PieSliceNode extends PNode {
    private final Paint color;

    public PieSliceNode( double startDegrees, double extentDegrees,

                         //The area which the entire pie should take up
                         Rectangle area, Paint color ) {
        this.color = color;
        removeAllChildren();

        PhetPPath path = new PhetPPath( new Arc2D.Double( area.x, area.y, area.width, area.height, startDegrees, extentDegrees, Arc2D.Double.PIE ),
                                        this.color, new BasicStroke( 2 ), Color.black );
        addChild( path );
    }
}