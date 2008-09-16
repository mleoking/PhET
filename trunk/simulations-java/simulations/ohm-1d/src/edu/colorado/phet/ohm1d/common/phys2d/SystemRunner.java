package edu.colorado.phet.ohm1d.common.phys2d;


public class SystemRunner implements Runnable {
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

    public void setAlive( boolean b ) {
        this.alive = b;
    }

    public void run() {
        this.alive = true;
        this.running = true;
        while ( alive ) {
            while ( running ) {
                system.iterate( dt );
                try {
                    Thread.sleep( waitTime );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep( notRunningWaitTime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning( boolean b ) {
        running = b;
    }
}



