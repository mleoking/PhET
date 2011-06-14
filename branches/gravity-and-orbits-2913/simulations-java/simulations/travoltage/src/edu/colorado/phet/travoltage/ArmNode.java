// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.*;
import java.awt.geom.Point2D;

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
        ImmutableVector2D v = Vector2D.parseAngleAndMagnitude( getImageNode().getWidth() * 0.95, getAngle() - insetAngle );
        return v.getDestination( globalPivot );
    }

    public Point2D getGlobalFingertipPointWithoutRotation() {
        Point2D globalPivot = localToGlobal( getPivot() );
        ImmutableVector2D v = Vector2D.parseAngleAndMagnitude( getImageNode().getWidth() * 0.95, 0 - insetAngle );
        return v.getDestination( globalPivot );
    }
}
