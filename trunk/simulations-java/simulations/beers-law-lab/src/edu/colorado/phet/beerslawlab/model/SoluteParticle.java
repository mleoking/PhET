// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Base class for solute particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SoluteParticle {

    //TODO if there's no difference between shaker and precipitate particles, make base class concrete and delete subclasses

    // One particle that comes out of the shaker.
    public static class ShakerParticle extends SoluteParticle {
        public ShakerParticle( Solute solute, Point2D location, double orientation ) {
            super( solute.precipitateColor, solute.particleSize, location, orientation );
        }
    }

    // One particle that makes up the precipitate that forms on the bottom of the beaker.
    public static class PrecipitateParticle extends SoluteParticle {
        public PrecipitateParticle( Solute solute, Point2D location, double orientation ) {
            super( solute.precipitateColor, solute.particleSize, location, orientation );
        }
    }

    private final Color color;
    private final double size; // particles are square, this is the length of one side
    private final Point2D location; // location of the particle in the coordinate frame of the beaker
    private final double orientation; // rotation angle, in radians

    public SoluteParticle( Color color, double size, Point2D location, double orientation ) {
        this.color = color;
        this.size = size;
        this.location = location;
        this.orientation = orientation;
    }

    public Color getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }

    public Point2D getLocation() {
        return new Point2D.Double( location.getX(), location.getY() );
    }

    public double getOrientation() {
        return orientation;
    }
}
