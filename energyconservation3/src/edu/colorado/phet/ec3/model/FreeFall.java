/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.Vector2D;


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
        boolean bodyCollidingWithSpline = body.getCollisionState().getSplineCount() > 0;
        boolean topOrig = body.getCollisionState().isTop();
        stepIgnoreSplines( body, dt );
        boolean topFinal = body.createCollisionState().isTop();
        if( topFinal != topOrig && bodyCollidingWithSpline ) {
            System.out.println( "Passed through spline" );
            new SplineInteraction( energyConservationModel ).doCollision( body.getCollisionState().getSpline( 0 ), body );
            int numTries = 3;
            int count = 0;
            while( topOrig != body.createCollisionState().isTop() && count < numTries ) {
                stepIgnoreSplines( body, dt );
                count++;
                System.out.println( "Restoration count = " + count );
            }
        }
        else {
            new SplineInteraction( energyConservationModel ).interactWithSplines( body );
        }
    }

    public void stepIgnoreSplines( Body body, double dt ) {
        double origEnergy = body.getTotalEnergy();
        setNetForce( getTotalForce( body ) );
        super.stepInTime( body, dt );
        body.setCMRotation( body.getCMRotation() + body.getAngularVelocity() * dt );
        new EnergyConserver().fixEnergy( body, origEnergy );
        double DE = Math.abs( body.getMechanicalEnergy() - origEnergy );
        if( DE > 1E-6 ) {
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
