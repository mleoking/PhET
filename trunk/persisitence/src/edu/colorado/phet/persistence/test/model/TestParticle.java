/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.model;

import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.persistence.test.util.PersistentParticle;

/**
 * TestParticle
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestParticle extends PersistentParticle {
    private double elapsedTime;
    private double omega = 0.1;
    private double y0;
    private double amplitude = 50;


    public TestParticle() {
    }

    public void setY0( double y0 ) {
        this.y0 = y0;
    }

    public double getY0() {
        return y0;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime( double elapsedTime ) {
        this.elapsedTime = elapsedTime;
    }

    public void setInitialPosition( double x, double y ) {
        super.setPosition( x, y );
        y0 = y;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        elapsedTime += dt;
        double y = amplitude * Math.sin( elapsedTime * omega ) + y0;
        setPosition( getPosition().getX(), y );
    }
}
