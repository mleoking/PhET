/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;


/**
 * Model template.
 */
public class GravityAndOrbitsModel {

    private final GravityAndOrbitsClock clock;
    private final Body sun = new Body( "Sun" );

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
}
