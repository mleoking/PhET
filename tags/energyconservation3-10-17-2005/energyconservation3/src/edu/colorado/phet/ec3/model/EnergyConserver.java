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
    public void fixEnergy( EnergyConservationModel model, Body body, double origTotalEnergy ) {
        if( body.getThrust().getMagnitude() != 0 ) {
            return;
        }

        EC3Debug.debug( "body.getSpeed() = " + body.getSpeed() );
        EnergyDebugger.stepFinished( model, body, origTotalEnergy );
        double speedThreshold = 20;
//        double speedThreshold = 500;

        if( body.getSpeed() > speedThreshold ) {
            conserveEnergyViaV( model, body, origTotalEnergy );
//        EnergyDebugger.postProcessed( model, body, origTotalEnergy, "dV" );
            conserveEnergyViaH( model, body, origTotalEnergy );
        }
        else {
            conserveEnergyViaH( model, body, origTotalEnergy );
        }
//        conserveEnergyViaH( model, body, origTotalEnergy );
//        conserveEnergyViaH( model, body, origTotalEnergy );
//        EnergyDebugger.postProcessed( model, body, origTotalEnergy, "dH" );
        double finalEnergy = model.getTotalEnergy( body );
        double deTOT = finalEnergy - origTotalEnergy;
        EC3Debug.debug( "dETOT=" + deTOT );
    }

    private void conserveEnergyViaV( EnergyConservationModel model, Body body, double origTotalEnergy ) {
        double finalTotalEnergy = model.getTotalEnergy( body );
        double dE = finalTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dE = " + dE );
        //how can we put this change in energy back in the system?
        double dv = dE / body.getMass() / body.getSpeed();
        AbstractVector2D dvVector = body.getVelocity().getInstanceOfMagnitude( -dv );
        body.setVelocity( dvVector.getAddedInstance( body.getVelocity() ) );

        double modifiedTotalEnergy = model.getTotalEnergy( body );
        double dEMod = modifiedTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dEModV = " + dEMod );
    }

    private void conserveEnergyViaH( EnergyConservationModel model, Body body, double origTotalEnergy ) {
        double finalTotalEnergy = model.getTotalEnergy( body );
        double dE = finalTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dE = " + dE );
        double dh = dE / body.getMass() / model.getGravity();
        body.translate( 0, dh );
        double modifiedTotalEnergy = model.getTotalEnergy( body );
        double dEMod = modifiedTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dEModH = " + dEMod );
    }
}
