/*
 * Class: MonitorClock
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 18, 2002
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.util.ThreadHelper;
import edu.colorado.phet.graphics.MonitorPanel;

/**
 *
 */
public class MonitorClock implements Runnable {

    private MonitorPanel monitorPanel;
    private PhysicalSystem physicalSystem;
    boolean running = true;
    boolean alive;
    double dt;
    int waitTime;
    double timeLimit = Double.MAX_VALUE;
    double runningTime;

    public MonitorClock( MonitorPanel monitorPanel, PhysicalSystem physicalSystem ) {
        this( monitorPanel, physicalSystem, s_defaultWaitTime );
    }

    public MonitorClock( MonitorPanel monitorPanel, PhysicalSystem physicalSystem, int waitTime ) {
        this.monitorPanel = monitorPanel;
        this.physicalSystem = physicalSystem;
        this.waitTime = waitTime;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime( int waitTime ) {
        this.waitTime = waitTime;
    }

    public boolean isActiveAndRunning() {
        return alive && running;
    }

    public void start() {
        Thread t = new Thread( this );
        t.setPriority( Thread.MIN_PRIORITY );
        t.start();
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
        runningTime = 0;
        while( alive ) {
            while( running ) {
                monitorPanel.update( physicalSystem, null );
                ThreadHelper.quietNap( waitTime );
            }
        }
    }

    //
    // Static fields and methods
    //
    private static int s_defaultWaitTime = 500;
}



