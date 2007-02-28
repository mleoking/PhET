/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.physics2d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 3, 2003
 * Time: 5:32:21 PM
 * Copyright (c) Jun 3, 2003 by Sam Reid
 */
public class ConstantForce implements Force {
    double x;
    double y;

    public ConstantForce(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D.Double getForce() {
        return new Point2D.Double(x, y);
    }
}
