package edu.colorado.phet.common.phys2d;


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
        //edu.colorado.phet.common.util.Debug.traceln("DT="+dt);
    }

    public void setWaitTime( int waitTime ) {
        this.waitTime = waitTime;
        //edu.colorado.phet.common.util.Debug.traceln("WaitTime="+waitTime);
    }

    public boolean isActiveAndRunning() {
        return alive && running;
    }

    public synchronized void setSystem( System2D system ) {
        this.system = system;
    }

    public synchronized void setTimeIncrement( double dt ) {
        this.dt = dt;
    }

    public void start() {
        new Thread( this ).start();
    }

    public void setAlive( boolean b ) {
        this.alive = b;
    }

    public void setRunning( boolean b ) {
        this.running = b;
    }

    public void stop() {
        setRunning( false );
        setAlive( false );
    }

    public void run() {
        this.alive = true;
        this.running = true;
        while( alive ) {
            while( running ) {
                system.iterate( dt );
                edu.colorado.phet.common.util.ThreadHelper.quietNap( waitTime );
            }
        }
    }
}



