/**
 * Class: Star
 * Package: edu.colorado.games4education.lostinspace.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.phet.common.model.ModelElement;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public abstract class Star implements ModelElement {
    private static Random random = new Random();
    private double luminance;
    private Point2D.Double location;
    private double z;
    private Color color;

    public Star( Color color, double luminance, Point2D.Double location, double syntheticZBound ) {
        this.luminance = luminance;
        this.location = location;
//        setZ( ( random.nextDouble() * syntheticZBound / 2 ) * ( random.nextBoolean() ? 1 : -1 ) );
        setZ( syntheticZBound );
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public double getLuminance() {
        return luminance;
    }

    public void setLuminance( double luminance ) {
        this.luminance = luminance;
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation( Point2D.Double location ) {
        this.location = location;
    }

    public double getZ() {
        return z;
    }

    public void setZ( double z ) {
        this.z = z;
    }

    public void stepInTime( double dt ) {
    }
}
