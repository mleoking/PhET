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

    public Ground( double angle, double length, double x0, double y0 ) {
        super( angle, length, x0, y0 );
    }

    public Surface copyState() {
        return new Ground( getAngle(), getLength(), getOrigin().getX(), getOrigin().getY() );
    }

    public void applyBoundaryConditions( RampModel rampModel, Block block ) {
        if( block.getPosition() < 0 ) {
//            block.setSurface( rampModel.getGround() );
            block.setPosition( 0 );
            block.setVelocity( 0 );
//            position = 0;
//            velocity = 0;
            //todo fire a collision.
        }
        else if( block.getPosition() > getLength() ) {
            block.setSurface( rampModel.getRamp() );
            block.setPosition( 0.0 );
            //todo fire a collision.
        }
    }
}
