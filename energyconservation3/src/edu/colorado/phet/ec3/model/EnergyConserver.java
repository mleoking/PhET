/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 11:38:49 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class EnergyConserver {
    public void fixEnergy( Body body, double desiredTotalEnergy ) {
        if( body.isUserControlled() ) {
            return;
        }
        fixEnergyWithVelocity( body, desiredTotalEnergy, 10 );
//        if( Math.abs( body.getGravity() ) > 1.0 ) {
//            for( int i = 0; i < 3; i++ ) {
//                boolean done = conserveEnergyViaH( body, desiredTotalEnergy );
//                if( done ) {
//                    break;
//                }
//            }
//        }
//        double mechEnergy = model.getMechanicalEnergy( body );
//        System.out.println( "requested mechEnergy = " + desiredMechanicalEnergy + ", obtained me=" + mechEnergy );
    }

    public boolean fixEnergyWithVelocity( Body body, double desiredTotalEnergy, int numIterations ) {
        if( body.isUserControlled() ) {
            return true;
        }
        double speedThreshold = 0;//reduced from 20.
        for( int i = 0; i < numIterations; i++ ) {
            if( body.getSpeed() > speedThreshold ) {
                boolean done = conserveEnergyViaV( body, desiredTotalEnergy );
                if( done ) {
                    return true;
                }
            }
            else {
            }
        }
        return false;
    }

    private boolean conserveEnergyViaV( Body body, double desiredTotalEnergy ) {
        double dE = getDE( body, desiredTotalEnergy );
        if( dE == 0 ) {
            return true;
        }
        //alter the velocity to account for this difference.
//        double dv = dE / body.getMass() / body.getSpeed();
        double dv = dE / body.getMass() / body.getSpeed();
        AbstractVector2D dvVector = body.getVelocity().getInstanceOfMagnitude( -dv );
        body.setVelocity( dvVector.getAddedInstance( body.getVelocity() ) );
        return false;
    }

    private double getDE( Body body, double desiredTotalEnergy ) {
        return body.getTotalEnergy() - desiredTotalEnergy;
    }

    private boolean conserveEnergyViaH( Body body, double desiredMechEnergy ) {
        double dE = getDE( body, desiredMechEnergy );
        if( dE == 0 ) {
            return true;
        }
        double dh = dE / body.getMass() / body.getGravity();
        body.translate( 0, dh );
        return false;
//        System.out.println( "------->requested mechEnergy = " + desiredMechEnergy+ ", obtained me=" + model.getMechanicalEnergy( body ));
    }
}
