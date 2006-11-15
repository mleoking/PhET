/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.util.ArrayList;


/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:57 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeFall extends ForceMode implements Derivable {
    private EnergyConservationModel energyConservationModel;

    public FreeFall( EnergyConservationModel energyConservationModel ) {
        this.energyConservationModel = energyConservationModel;
    }

    public void stepInTime( Body body, double dt ) {
        Body.StateRecord origState = body.getCollisionState();
        stepIgnoreSplines( body, dt );
        Body.StateRecord newState = body.createCollisionState();
        AbstractSpline passedThrough = getCrossedSpline( origState, newState );
        if( passedThrough != null ) {
//            System.out.println( "Passed through spline, at first, top=" + topOrig + ", but now top=" + topFinal );
            System.out.println( "Passed through spline: " + passedThrough );
            new SplineInteraction( energyConservationModel ).doCollision( passedThrough, body );
            int maxTries = 2;
            int count = 0;
            while( getCrossedSpline( origState, body.createCollisionState() ) != null && count < maxTries ) {                  //todo: should we set it so this spline can't be grabbed soon?
                stepIgnoreSplines( body, dt );
                count++;
                System.out.println( "Restoration count = " + count );
            }
//            body.clearCollisionHistory();
        }
        else {
            new SplineInteraction( energyConservationModel ).interactWithSplines( body );
        }
    }

    private AbstractSpline getCrossedSpline( Body.StateRecord a, Body.StateRecord b ) {
        ArrayList splines = new ArrayList();
        for( int i = 0; i < a.getSplineCount(); i++ ) {
            AbstractSpline spline = a.getSpline( i );
            if( b.containsSpline( spline ) ) {
                splines.add( spline );
            }
        }
        for( int i = 0; i < splines.size(); i++ ) {
            AbstractSpline spline = (AbstractSpline)splines.get( i );
            if( isCrossed( a.getTraversalState( spline ), b.getTraversalState( spline ) ) ) {
                return spline;
            }
        }
        return null;
    }

    private boolean isCrossed( Body.TraversalState x, Body.TraversalState y ) {
        if( x.isTop() == y.isTop() ) {
            //not a crossing, since they were both on the same side.
            return false;
        }
        double epsilon = 0.02;
        if( x.getScalarAlongSpline() <= 0 || y.getScalarAlongSpline() <= 0 || x.getScalarAlongSpline() >= x.getSpline().getLength() - epsilon || y.getScalarAlongSpline() >= y.getSpline().getLength() - epsilon ) {
            //bogus result because we passed the end of the spline
            return false;
        }
        if( x.getClosestSplineLocation().distance( y.getClosestSplineLocation() ) > 2 ) {
            //bogus because the spline locations didn't match.
            return false;
        }

        return true;
    }

    public void stepIgnoreSplines( Body body, double dt ) {
        double origEnergy = body.getTotalEnergy();
        setNetForce( getTotalForce( body ) );
        super.stepInTime( body, dt );
        body.setCMRotation( body.getCMRotation() + body.getAngularVelocity() * dt );
        if( !body.isUserControlled() ) {
            new EnergyConserver().fixEnergyWithVelocity( body, origEnergy, 10, 0.01 );
        }
//        new EnergyConserver().conserveEnergyViaH( body, origEnergy );
        double DE = Math.abs( body.getTotalEnergy() - origEnergy );
        if( DE > 1E-6 && !body.isUserControlled() ) {
            System.out.println( "energy conservation error in free fall: " + DE );
        }
    }

    private Vector2D.Double getTotalForce( Body body ) {
        return new Vector2D.Double( 0 + body.getThrust().getX(), body.getMass() * body.getGravity() + body.getThrust().getY() );
    }

    public void init( Body body ) {
        //going from spline to freefall mode
        body.convertToFreefall();
    }

    public UpdateMode copy() {
        return new FreeFall( energyConservationModel );
    }
}
