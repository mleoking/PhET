// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Fulcrum (for lack of a better word) that has a pivot point that is above
 * the plank.  This looks sort of like a swingset, with angled legs that go
 * from the ground up to apex in a sort of A-frame arrangement.
 *
 * @author John Blanco
 */
public class FulcrumAbovePlank extends ShapeModelElement {
    //Distance from the base of the fulcrum to its top
    private static final double LEG_THICKNESS_FACTOR = 0.1;

    public FulcrumAbovePlank( double width, double height ) {
        super( generateShape( width, height ) );
    }

    private static final Shape generateShape( double width, double height ) {
        double legThickness = LEG_THICKNESS_FACTOR * width;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( -width / 2, 0 );
        path.lineTo( 0, height );
        path.lineTo( width / 2, 0 );
        path.lineTo( width / 2 - legThickness, 0 );
        path.lineTo( 0, ( width - 2 * legThickness ) / width * height );
        path.lineTo( -width / 2 + legThickness, 0 );
        path.closePath();
        return path.getGeneralPath();
    }
}
