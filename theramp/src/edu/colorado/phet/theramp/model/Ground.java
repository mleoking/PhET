/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

/**
 * User: Sam Reid
 * Date: May 17, 2005
 * Time: 1:26:37 AM
 * Copyright (c) May 17, 2005 by Sam Reid
 */

public class Ground extends Surface {
    public Ground( double angle, double length ) {
        super( angle, length );
    }

    public Ground( double angle, double length, double x0, double y0, double distanceOffset ) {
        super( angle, length, x0, y0, distanceOffset );
    }

    public Surface copyState() {
        return new Ground( getAngle(), getLength(), getOrigin().getX(), getOrigin().getY(), getDistanceOffset() );
    }

    public void applyBoundaryConditions( RampModel rampModel, Block block ) {
        if( block.getPositionInSurface() < 0 ) {
            block.setPositionInSurface( 0 );
            block.setVelocity( 0 );
            super.notifyCollision();
        }
        else if( block.getPositionInSurface() > getLength() ) {
            block.setSurface( rampModel.getRamp() );
            block.setPositionInSurface( 0.0 );
        }
    }


    public double getWallForce( double sumOtherForces, Block block ) {
        if( block.getPositionInSurface() == 0.0 && sumOtherForces < 0 ) {
            return -sumOtherForces;
        }
        else {
            return 0.0;
        }
    }

}
