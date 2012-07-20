// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:03:58 AM
 */

public class ArmNode extends LimbNode {
    private double insetAngle = Math.PI / 16 + Math.PI / 32;

    public ArmNode() {
        super( "travoltage/images/arm2.gif", new Point( 8, 42 ) );
    }

    public Point2D getGlobalFingertipPoint() {
        Point2D globalPivot = localToGlobal( getPivot() );
        ImmutableVector2D v = MutableVector2D.createPolar( getImageNode().getWidth() * 0.95, getAngle() - insetAngle );
        return v.getDestination( globalPivot );
    }

    public Point2D getGlobalFingertipPointWithoutRotation() {
        Point2D globalPivot = localToGlobal( getPivot() );
        ImmutableVector2D v = MutableVector2D.createPolar( getImageNode().getWidth() * 0.95, 0 - insetAngle );
        return v.getDestination( globalPivot );
    }
}
