/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.mri.util.IScalar;

import java.awt.geom.Point2D;

/**
 * PlaneWaveMedium
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlaneWaveMedium extends SimpleObservable implements ModelElement {

    public static class Direction extends Vector2D.Double {
        private Direction( double dx, double dy ) {
            super( dx, dy );
        }
    }

    public static Direction NORTH = new Direction( 0, -1 );
    public static Direction SOUTH = new Direction( 0, 1 );
    public static Direction EAST = new Direction( 1, 0 );
    public static Direction WEST = new Direction( 0, -1 );


    private IScalar source;
    private Point2D origin;
    private double length;
    private Direction direction;
    private double speed;
    private double[] values;

    /**
     * @param source
     * @param origin
     * @param length    length of the medium in distance units
     * @param direction
     * @param speed     number of distance units the wave travels in a time unit
     */
    public PlaneWaveMedium( IScalar source, Point2D origin, double length, Direction direction, double speed ) {
        this.source = source;
        this.origin = origin;
        this.length = length;
        this.direction = direction;
        this.speed = speed;

        values = new double[ (int)( length / speed )];
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin( Point2D origin ) {
        this.origin = origin;
    }

    public void setSpeed( double speed ) {
        this.speed = speed;
    }

    public double getLength() {
        return length;
    }

    public void setLength( double length ) {
        this.length = length;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection( Direction direction ) {
        this.direction = direction;
    }

    public void stepInTime( double dt ) {
        double newValue = source.getValue();
        for( int i = values.length - 1; i > 0; i-- ) {
            values[i] = values[i - 1];
        }
        values[0] = newValue;
        notifyObservers();
    }

    public double getAmplitudeAt( double x ) {
        return values[(int)( x / speed )];
    }

    public double getSpeed() {
        return speed;
    }
}
