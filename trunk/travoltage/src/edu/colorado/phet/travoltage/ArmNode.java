/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:03:58 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class ArmNode extends LimbNode {
    private double insetAngle = Math.PI / 16 + Math.PI / 32;

    public ArmNode() {
        super( "images/arm2.gif", new Point( 8, 42 ) );
    }

    public Point2D getGlobalFingertipPoint() {
        Point2D globalPivot = localToGlobal( getPivot() );
        AbstractVector2D v = Vector2D.Double.parseAngleAndMagnitude( getImageNode().getWidth() * 0.95, getAngle() - insetAngle );
        return v.getDestination( globalPivot );
    }

    public Point2D getGlobalFingertipPointWithoutRotation() {
        Point2D globalPivot = localToGlobal( getPivot() );
        AbstractVector2D v = Vector2D.Double.parseAngleAndMagnitude( getImageNode().getWidth() * 0.95, 0 - insetAngle );
        return v.getDestination( globalPivot );
    }
}
