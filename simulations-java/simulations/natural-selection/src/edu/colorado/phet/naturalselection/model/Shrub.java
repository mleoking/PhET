package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.view.NaturalSelectionSprite;

public class Shrub {

    private final double backgroundX;
    private final double backgroundY;
    private final double baseScale;

    private final double x;
    private final double y;
    private final double z;

    private final Point3D position;

    public Shrub( double backgroundX, double backgroundY, double baseScale ) {
        this.backgroundX = backgroundX;
        this.backgroundY = backgroundY;
        this.baseScale = baseScale;

        x = backgroundX;
        y = 0;
        z = NaturalSelectionSprite.getInverseGroundZDepth( y );

        position = new Point3D.Double( x, y, z );
    }

    public double getBackgroundX() {
        return backgroundX;
    }

    public double getBackgroundY() {
        return backgroundY;
    }

    public double getBaseScale() {
        return baseScale;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Point3D getPosition() {
        return position;
    }
}
