/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.Vector2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:43 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class UserControlled implements UpdateMode {

    public UserControlled() {
    }

    public void stepInTime( Body body, double dt ) {
        double deltaThermal = 500;
        body.setThermalEnergy( Math.max( body.getThermalEnergy() - deltaThermal, 0.0 ) );
    }

    public void init( Body body ) {
        body.convertToFreefall();
        body.setVelocity( new Vector2D.Double( 0, 0 ) );
    }

    public UpdateMode copy() {
        return new UserControlled();
    }

    public void finish( Body body ) {
        body.setThermalEnergy( 0.0 );
    }

}
