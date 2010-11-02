/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;


/**
 * Model template.
 */
public class GravityAndOrbitsModel {

    private final GravityAndOrbitsClock clock;
    private final Body sun = new Body( "Sun", 0, 0, 50, Color.yellow, Color.white );
    private final Body planet = new Body( "Planet", 0, 0, 10, Color.blue, Color.white );

    public GravityAndOrbitsModel( GravityAndOrbitsClock clock ) {
        super();
        this.clock = clock;
    }

    public GravityAndOrbitsClock getClock() {
        return clock;
    }

    public Body getSun() {
        return sun;
    }

    public Body getPlanet() {
        return planet;
    }
}
