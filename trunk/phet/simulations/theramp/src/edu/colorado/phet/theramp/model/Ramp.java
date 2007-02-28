/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.theramp.TheRampStrings;

/**
 * User: Sam Reid
 * Date: May 17, 2005
 * Time: 1:26:22 AM
 * Copyright (c) May 17, 2005 by Sam Reid
 */

public class Ramp extends Surface {
    public Ramp( double angle, double length ) {
        super( angle, length );
    }

    public Ramp( double angle, double length, double x0, double y0, double distanceOffset ) {
        super( angle, length, x0, y0, distanceOffset );
    }

    public Surface copyState() {
        return new Ramp( getAngle(), getLength(), getOrigin().getX(), getOrigin().getY(), getDistanceOffset() );
    }

    public boolean applyBoundaryConditions( RampPhysicalModel rampPhysicalModel, Block block ) {
        if( block.getPositionInSurface() < 0 ) {
            block.setSurface( rampPhysicalModel.getGround() );
            double overshoot = -block.getPositionInSurface();
            if( overshoot > rampPhysicalModel.getGround().getLength() ) {
                overshoot = rampPhysicalModel.getGround().getLength();
            }
            block.setPositionInSurface( rampPhysicalModel.getGround().getLength() - overshoot );
        }
        else if( block.getPositionInSurface() > getLength() ) {
            block.setPositionInSurface( getLength() );
            block.setVelocity( 0.0 );
            super.notifyCollision();
            return true;
        }
        return false;
    }

    public double getWallForce( double sumOtherForces, Block block ) {
        if( block.getPositionInSurface() == getLength() && sumOtherForces > 0 ) {
            return -sumOtherForces;
        }
        else {
            return 0.0;
        }
    }

    public String getName() {
        return TheRampStrings.getString( "ramp" );
    }
}
