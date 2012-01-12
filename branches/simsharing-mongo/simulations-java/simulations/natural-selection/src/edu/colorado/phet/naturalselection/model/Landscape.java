// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

/**
 * Represents both a landscape itself (ground plane), but also the coordinates and transformations from this 3d
 * system to and from scaled screen coordinates. (Scaled, so that the "screen" coordinates are independent of how
 * the play area is resized)
 * <p/>
 * The actual ground is a plane, sloped so that it looks like it is a hillside.
 *
 * @author Jonathan Olson
 */
public class Landscape {
    /**
     * The pixel-level from the top of the "horizon", where the 3d bunny positions would appear if infinitely far away
     */
    public static final double HORIZON = 120.0;

    /**
     * Static "landscape" size. Bunny (and other sprite) 3d coordinates are contained within this landscape coordinate
     * system, irregardless of the shape of the viewing window
     */
    public static final Dimension SIZE = NaturalSelectionDefaults.VIEW_SIZE;

    /**
     * This is as close as bunnies can get to the "camera". Essentially the bottom and front of the ground
     */
    public static final double NEARPLANE = 150;

    /**
     * This is as far as bunnies can get from the "camera". Essentially the top and the back of the ground
     */
    public static final double FARPLANE = 300;

    /**
     * Total vertical rise of the ground plane from the front to the back.
     */
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

    /**
     * Returns a random position on the ground. Since the ground display is similar to a trapezoid, we sample it as such
     *
     * @return Random 3d ground position
     */
    public Point3D getRandomGroundPosition() {
        // randomly sample the trapezoid in the z direction
        double z = Math.sqrt( Landscape.NEARPLANE * Landscape.NEARPLANE + Math.random() * ( Landscape.FARPLANE * Landscape.FARPLANE - Landscape.NEARPLANE * Landscape.NEARPLANE ) );

        double x = getMaximumX( z ) * ( Math.random() * 2 - 1 );
        double y = getGroundY( x, z );

        return new Point3D.Double( x, y, z );
    }

    /**
     * Returns the ground position underneath the x and z coordinates
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Full 3D position of the ground under the location above (same x and z)
     */
    public Point3D getGroundPoint( double x, double z ) {
        return new Point3D.Double( x, getGroundY( x, z ), z );
    }

    /**
     * Returns the ground height under a certain position
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return The ground height (Y value) at the specific position
     */
    public double getGroundY( double x, double z ) {
        return ( z - Landscape.FARPLANE ) * Landscape.VERTICAL_RISE / ( Landscape.FARPLANE - Landscape.NEARPLANE );
    }

    /**
     * Returns the maximum X value for a particular depth. This actually varies on depth, since the bunnies are (in 3d)
     * laid out on a trapezoidal field.
     *
     * @param z The depth
     * @return The maximum X value. The minimum X value is this value negated.
     */
    public double getMaximumX( double z ) {
        return z * Landscape.SIZE.getWidth() * 0.5 / getFactor();
    }

    /**
     * Common scaling factor
     *
     * @return Common scaling factor
     */
    private double getFactor() {
        return ( Landscape.SIZE.getHeight() - Landscape.HORIZON ) * Landscape.NEARPLANE / Landscape.VERTICAL_RISE;
    }

    /**
     * Given a particular (screen) height, return the depth of the ground slope which has this same height.
     *
     * @param yy The visual screen height (scaled)
     * @return The 3d ground depth shown at that point on the screen
     */
    public double landscapeYToZ( double yy ) {
        return ( Landscape.NEARPLANE * Landscape.FARPLANE * ( Landscape.HORIZON - Landscape.SIZE.getHeight() ) ) / ( Landscape.FARPLANE * ( Landscape.HORIZON - yy ) + Landscape.NEARPLANE * ( yy - Landscape.SIZE.getHeight() ) );
    }

    /**
     * Given a screen X value and a 3d depth Z, return the 3d X value
     *
     * @param x Screen (scaled) X
     * @param z 3D (model) depth Z
     * @return 3D (model) X
     */
    public double landscapeXmodelZToX( double x, double z ) {
        return z * ( x - SIZE.getWidth() / 2 ) / getFactor();
    }

    /**
     * Given scaled screen coordinates (x,y), return the 3D position on the ground
     *
     * @param x Scaled screen X
     * @param y Scaled screen Y
     * @return 3D ground position where the user would have clicked
     */
    public Point3D landscapeToModel( double x, double y ) {
        double retZ = landscapeYToZ( y );
        double retX = landscapeXmodelZToX( x, retZ );
        double retY = getGroundY( retX, retZ );
        return new Point3D.Double( retX, retY, retZ );
    }

    /**
     * Given a 3D position, project it into scaled screen coordinates
     *
     * @param position 3D position
     * @return Scaled screen coordinates (x,y)
     */
    public Point2D spriteToScreen( Point3D position ) {
        double landscapeX = Landscape.SIZE.getWidth() / 2 + ( position.getX() / position.getZ() ) * getFactor();
        double landscapeY = Landscape.HORIZON - ( position.getY() / position.getZ() ) * getFactor();
        return new Point2D.Double( landscapeX * landscapeWidth / Landscape.SIZE.getWidth(), landscapeY * landscapeHeight / Landscape.SIZE.getHeight() );
    }

    /**
     * Turn a scaled screen distance into a model distance at a particular depth Z
     *
     * @param distance Scaled screen distance
     * @param z        Model depth Z
     * @return Model distance
     */
    public double landscapeDistanceToModel( double distance, double z ) {
        return distance * z / getFactor();
    }

}
