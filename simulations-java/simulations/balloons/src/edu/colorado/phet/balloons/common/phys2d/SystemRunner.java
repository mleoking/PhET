package edu.colorado.phet.balloons.common.phys2d;


public class SystemRunner implements Runnable {
    System2D system;
    boolean running = true;
    boolean alive;
    double dt;
    int waitTime;

    public SystemRunner( System2D system, double dt, int waitTime ) {
        this.system = system;
        this.dt = dt;
        this.waitTime = waitTime;
    }

    public void run() {
        this.alive = true;
        this.running = true;
        while( alive ) {
            while( running ) {
                iterate();
                try {
                    Thread.sleep(waitTime);
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void iterate() {
        system.iterate( dt );
    }
}



