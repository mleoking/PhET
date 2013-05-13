// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.Shape;

import edu.colorado.phet.balanceandtorquestudy.common.model.ShapeModelElement;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * This is a column that can be used to support one of the ends of the plank
 * in a tilted position.  At the time of this writing, this type of column is
 * always used alone and holds the plank in a position where the other end is
 * on the ground.
 *
 * @author John Blanco
 */
public class TiltSupportColumn extends ShapeModelElement {

    // Length of the base of the column
    private static final double WIDTH = 0.35;

    /**
     * Constructor.
     *
     * @param height
     * @param initialCenterX
     * @param topAngle       - Angle of slant for the top of the support.  Positive
     *                       values cause a tilt down to the right, negative angle tilt down to the
     *                       left.
     */
    public TiltSupportColumn( double height, double initialCenterX, double topAngle ) {
        super( createShape( initialCenterX, height, topAngle ) );
    }

    public static Shape createShape( double centerX, double height, double topAngle ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( centerX - WIDTH / 2, 0 );
        path.lineTo( centerX - WIDTH / 2, height - WIDTH / 2 * Math.tan( -topAngle ) );
        path.lineTo( centerX + WIDTH / 2, height + WIDTH / 2 * Math.tan( -topAngle ) );
        path.lineTo( centerX + WIDTH / 2, 0 );
        path.closePath();
        return path.getGeneralPath();
    }
}
