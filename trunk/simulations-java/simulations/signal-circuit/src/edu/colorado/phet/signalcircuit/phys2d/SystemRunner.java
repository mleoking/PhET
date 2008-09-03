package edu.colorado.phet.signalcircuit.phys2d;


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

    public void setDt( double dt ) {
        this.dt = dt;
        //edu.colorado.phet.util.Debug.traceln("DT="+dt);
    }

    public void setWaitTime( int waitTime ) {
        this.waitTime = waitTime;
        //edu.colorado.phet.util.Debug.traceln("WaitTime="+waitTime);
    }

    public void run() {
        this.alive = true;
        this.running = true;
        while( alive ) {
            while( running ) {
                system.iterate( dt );
                try {
                    Thread.sleep( waitTime );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}



