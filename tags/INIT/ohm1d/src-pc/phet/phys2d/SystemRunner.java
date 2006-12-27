package phet.phys2d;


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
        while( alive ) {
            while( running ) {
                system.iterate( dt );
                util.ThreadHelper.quietNap( waitTime );
            }
            util.ThreadHelper.quietNap( notRunningWaitTime );
        }
    }

    public void setRunning( boolean b ) {
        running=b;
    }
}



