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
    private double rotationalVelocity;

    public double getRotationalVelocity() {
        return rotationalVelocity;
    }

    public void setRotationalVelocity( double rotationalVelocity ) {
        this.rotationalVelocity = rotationalVelocity;
    }

    public FreeFall( double rotationalVelocity ) {
        this.rotationalVelocity = rotationalVelocity;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        double origEnergy = model.getMechanicalEnergy( body );
        setNetForce( getTotalForce( body, model ) );
        super.stepInTime( model, body, dt );
        body.setCMRotation( body.getCMRotation() + rotationalVelocity * dt );
        new EnergyConserver().fixEnergy( model, body, origEnergy );
        double DE = Math.abs( model.getMechanicalEnergy( body ) - origEnergy );
        if( DE > 1E-6 ) {
            System.out.println( "energy conservation error in free fall: " + DE );
        }
    }

    private Vector2D.Double getTotalForce( Body body, EnergyConservationModel model ) {
        return new Vector2D.Double( 0 + body.getThrust().getX(), body.getMass() * model.getGravity() + body.getThrust().getY() );
    }

    public void reset() {
        rotationalVelocity = 0.0;
    }

    public void init( EnergyConservationModel model, Body body ) {
        //going from spline to freefall mode
//        FreeSplineMode.State state = new FreeSplineMode.State( model, body );
        body.convertToFreefall();
    }
}
