/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:38:26 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class WalkMotion implements StepMotion {
    private double vel = 0;
    private MovingManModule module;

    public WalkMotion( MovingManModule module ) {
        this.module = module;
    }

    //TODO this is awkward.
    public double stepInTime( Man man, double dt ) {
        double newPosition = man.getX() + vel * dt;
        module.getMan().setVelocity( vel );
        return newPosition;
    }

    public void setVelocity( double vel ) {
        this.vel = vel;
    }
}
