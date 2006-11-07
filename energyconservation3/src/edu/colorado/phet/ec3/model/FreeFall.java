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
        double origEnergy = body.getTotalEnergy();
        setNetForce( getTotalForce( body ) );
        super.stepInTime( body, dt );
        body.setCMRotation( body.getCMRotation() + body.getAngularVelocity() * dt );
        new EnergyConserver().fixEnergy( body, origEnergy );
        double DE = Math.abs( body.getMechanicalEnergy() - origEnergy );
        if( DE > 1E-6 ) {
            System.out.println( "energy conservation error in free fall: " + DE );
        }
        new SplineMode.GrabSpline( energyConservationModel ).interactWithSplines( body );
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
