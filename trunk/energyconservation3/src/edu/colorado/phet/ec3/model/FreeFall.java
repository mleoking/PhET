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
        double origEnergy = model.getTotalMechanicalEnergy( body );
        setNetForce( getTotalForce( body, model ) );

//        this.doStepInTime( model, body, dt );
        super.stepInTime( model, body, dt );

//        System.out.println( "dEStep = " + ( model.getTotalMechanicalEnergy( body ) - origEnergy ) );

        body.setAngle( body.getAngle() + rotationalVelocity * dt );
        new EnergyConserver().fixEnergy( model, body, origEnergy );
//        double finalEnergy = model.getTotalMechanicalEnergy( body );
//        double dE = finalEnergy - origEnergy;
//        System.out.println( "dE = " + dE );
    }

//    private void doStepInTimeORIG( EnergyConservationModel model, Body body, double dt ) {
//        AbstractVector2D acceleration = getNetForce().getScaledInstance( 1.0 / body.getMass() );
//        AbstractVector2D velocity = body.getVelocity().getAddedInstance( acceleration.getScaledInstance( dt ) );
//        Point2D newPosition = new Point2D.Double( body.getX() + velocity.getX() * dt, body.getY() + velocity.getY() * dt );
//        body.setState( acceleration, velocity, newPosition );
//    }
//
//    public void stepInTimeORIG( EnergyConservationModel model, Body body, double dt ) {
//        double origEnergy = model.getTotalEnergy( body );
//        setNetForce( getTotalForce( body, model ) );
//
//        super.stepInTime( model, body, dt );
//        body.setAngle( body.getAngle() + rotationalVelocity * dt );
//        new EnergyConserver().fixEnergy( model, body, origEnergy );
//    }

    private Vector2D.Double getTotalForce( Body body, EnergyConservationModel model ) {
        return new Vector2D.Double( 0 + body.getThrust().getX(), body.getMass() * model.getGravity() + body.getThrust().getY() );
    }
}
