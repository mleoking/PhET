/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:42:58 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public class OscMotion implements StepMotion {
    double k;//spring constant.
    double center = 0;
    private double initX = 5;
    private MovingManModule module;

    public OscMotion( MovingManModule module, double k ) {
        this.module = module;
        this.k = k;
    }

    public double stepInTime( Man man, double dt ) {
        dt = dt * MovingManModule.TIMER_SCALE;
        double x = man.getX() - center;
        double acceleration = -k * x;
        double vnew = module.getMan().getVelocity() + acceleration * dt;
        module.getMan().setVelocity( vnew );
        double xnew = man.getX() + vnew * dt;
        return xnew;
    }

    public void setK( double k ) {
        this.k = k;
    }

    public void initialize( Man man ) {
        module.getMan().setVelocity( 0 );
        man.setX( initX );
    }
}
