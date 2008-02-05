/*  */
package edu.colorado.phet.theramp.model;

/**
 * User: Sam Reid
 * Date: May 17, 2005
 * Time: 1:26:37 AM
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

    public boolean applyBoundaryConditions( RampPhysicalModel rampPhysicalModel, Block block ) {
        if( block.getPositionInSurface() < 0 ) {
            block.setPositionInSurface( 0 );
            block.setVelocity( 0 );
            super.notifyCollision();
            return true;
        }
        else if( block.getPositionInSurface() > getLength() ) {
            block.setSurface( rampPhysicalModel.getRamp() );
            double overshoot = block.getPositionInSurface() - getLength();
            if( overshoot > rampPhysicalModel.getRamp().getLength() ) {
                overshoot = rampPhysicalModel.getRamp().getLength();
            }
            block.setPositionInSurface( overshoot );
        }
        return false;
    }


    public double getWallForce( double sumOtherForces, Block block ) {
        if( block.getPositionInSurface() == 0.0 && sumOtherForces < 0 ) {
            return -sumOtherForces;
        }
        else {
            return 0.0;
        }
    }

    public String getName() {
        return getClass().getName();
    }

}
