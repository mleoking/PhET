// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield;

public class SystemRunner {
    private System2D system;

    public SystemRunner( System2D system2d ) {
        system = system2d;
    }

    public synchronized void setSystem( System2D system2d ) {
        system = system2d;
    }

    public void step( double dt ) {
        system.iterate( dt );
    }

}
