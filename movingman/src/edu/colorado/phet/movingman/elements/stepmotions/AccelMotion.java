/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:37:53 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class AccelMotion implements StepMotion {
    double accel = 0;
    private MovingManModule module;

    public AccelMotion( MovingManModule module ) {
        this.module = module;
    }

    public AccelMotion( MovingManModule module, double accel ) {
        this.module = module;
        this.accel = accel;
    }

    public double stepInTime( Man man, double dt ) {
        double velocity = module.getMan().getVelocity() + accel * dt;
        System.out.println( "velocity = " + velocity );
        module.getMan().setVelocity( velocity );
        double position = man.getX() + velocity * dt;
        return position;
    }

    public void setAcceleration( double accel ) {
        this.accel = accel;
    }
}
