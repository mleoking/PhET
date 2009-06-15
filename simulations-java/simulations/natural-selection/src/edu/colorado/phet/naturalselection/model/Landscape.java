package edu.colorado.phet.naturalselection.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

public class Landscape {
    /**
     * The pixel-level from the top of the "horizon", where the 3d bunny positions would appear if infinitely far away
     */
    public static final double HORIZON = 120.0;
    public static final Dimension SIZE = NaturalSelectionDefaults.VIEW_SIZE;
    public static final double NEARPLANE = 150;
    public static final double FARPLANE = 300;
    public static final double VERTICAL_RISE = 100;

    private double landscapeWidth;
    private double landscapeHeight;

    public Landscape() {
        landscapeWidth = SIZE.getWidth();
        landscapeHeight = SIZE.getHeight();
    }

    public Landscape( double landscapeWidth, double landscapeHeight ) {
        this.landscapeWidth = landscapeWidth;
        this.landscapeHeight = landscapeHeight;
    }

    public void updateSize( double width, double height ) {
        landscapeWidth = width;
        landscapeHeight = height;
    }

    public Point3D getRandomGroundPosition() {
        // randomly sample the trapezoid in the z direction
        double z = Math.sqrt( Landscape.NEARPLANE * Landscape.NEARPLANE + Math.random() * ( Landscape.FARPLANE * Landscape.FARPLANE - Landscape.NEARPLANE * Landscape.NEARPLANE ) );

        double x = getMaximumX( z ) * ( Math.random() * 2 - 1 );
        double y = getGroundY( x, z );

        return new Point3D.Double( x, y, z );
    }

    public Point3D getGroundPoint( double x, double z ) {
        return new Point3D.Double( x, getGroundY( x, z ), z );
    }

    public double getGroundY( double x, double z ) {
        return ( z - Landscape.FARPLANE ) * Landscape.VERTICAL_RISE / ( Landscape.FARPLANE - Landscape.NEARPLANE );
    }

    public double getMaximumX( double z ) {
        return z * Landscape.SIZE.getWidth() * 0.5 / getFactor();
    }

    private double getFactor() {
        return ( Landscape.SIZE.getHeight() - Landscape.HORIZON ) * Landscape.NEARPLANE / Landscape.VERTICAL_RISE;
    }

    public double landscapeYToZ( double yy ) {
        return ( Landscape.NEARPLANE * Landscape.FARPLANE * ( Landscape.HORIZON - Landscape.SIZE.getHeight() ) ) / ( Landscape.FARPLANE * ( Landscape.HORIZON - yy ) + Landscape.NEARPLANE * ( yy - Landscape.SIZE.getHeight() ) );
    }

    public double landscapeXmodelZToX( double x, double z ) {
        return z * ( x - SIZE.getWidth() / 2 ) / getFactor();
    }

    public Point3D landscapeToModel( double x, double y ) {
        double retZ = landscapeYToZ( y );
        double retX = landscapeXmodelZToX( x, retZ );
        double retY = getGroundY( retX, retZ );
        return new Point3D.Double( retX, retY, retZ );
    }

    public Point2D spriteToScreen( Point3D position ) {
        double landscapeX = Landscape.SIZE.getWidth() / 2 + ( position.getX() / position.getZ() ) * getFactor();
        double landscapeY = Landscape.HORIZON - ( position.getY() / position.getZ() ) * getFactor();
        return new Point2D.Double( landscapeX * landscapeWidth / Landscape.SIZE.getWidth(), landscapeY * landscapeHeight / Landscape.SIZE.getHeight() );
    }

    public double landscapeDistanceToModel( double distance, double z ) {
        return distance * z / getFactor();
    }

}
