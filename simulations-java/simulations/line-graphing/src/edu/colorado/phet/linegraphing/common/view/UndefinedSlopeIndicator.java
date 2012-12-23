// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A translucent "X", to be placed on top of an equation whose slope is undefined.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class UndefinedSlopeIndicator extends PComposite {

    private static final BasicStroke STROKE = new BasicStroke( 4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER );
    private static final Paint STROKE_PAINT = new Color( 255, 0, 0, 80 );

    public UndefinedSlopeIndicator( double width, double height ) {
        setPickable( false );
        setChildrenPickable( false );
        addChild( new PhetPPath( new Line2D.Double( 0, 0, width, height ), STROKE, STROKE_PAINT ) );
        addChild( new PhetPPath( new Line2D.Double( 0, height, width, 0 ), STROKE, STROKE_PAINT ) );
    }
}
