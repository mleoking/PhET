package edu.colorado.phet.bernoulli.zoom;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 3:48:00 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class MoveTo {
    double target;
    double speed;

    public MoveTo( double target, double speed ) {
        this.target = target;
        this.speed = speed;
    }

    public double moveCloser( double currentLocation ) {
        double dx = target - currentLocation;
        if( Math.abs( dx ) < speed || dx == 0 ) {
            return target;//you made it!
        }
        else if( dx > 0 ) {
            return currentLocation + speed;
        }
        else if( dx < 0 ) {
            return currentLocation - speed;
        }
        else {
            throw new RuntimeException( "dx was neither positive, negative or zero." );
        }

    }
}
