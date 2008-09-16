package edu.colorado.phet.ohm1d.common.phys2d;


public class SystemRunner  {
    System2D system;
    boolean running = true;
    boolean alive;
    double dt;
    int waitTime;
    int notRunningWaitTime = 50;

    public SystemRunner( System2D system, double dt, int waitTime ) {
        this.system = system;
        this.dt = dt;
        this.waitTime = waitTime;
    }

    public void step() {
        system.iterate( dt );
    }

    public void setRunning( boolean b ) {
        running = b;
    }
}



