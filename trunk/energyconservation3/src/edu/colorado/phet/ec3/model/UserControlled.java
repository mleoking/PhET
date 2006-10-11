/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:43 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class UserControlled implements UpdateMode {
    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
    }

    public void init( EnergyConservationModel model, Body body ) {
//        body.setCMRotation( 0 );
//        body.setAttachmentPointRotation( Math.PI );
        body.convertToFreefall();
    }

}
