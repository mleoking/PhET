// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.common.phys2d;


public class SystemRunner {
    System2D system;
    double dt;

    public SystemRunner( System2D system, double dt ) {
        this.system = system;
        this.dt = dt;
    }

    public void step() {
        system.iterate( dt );
    }

}



