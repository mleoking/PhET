// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.phys2d_efield;

import edu.colorado.phet.efield.electron.utils.ThreadHelper;

// Referenced classes of package phys2d:
//            System2D

public class SystemRunner
        implements Runnable {

    public SystemRunner( System2D system2d, double d, int i ) {
        running = true;
        system = system2d;
        dt = d;
        waitTime = i;
    }

    public boolean isActiveAndRunning() {
        return alive && running;
    }

    public synchronized void setSystem( System2D system2d ) {
        system = system2d;
    }

    public void setRunning( boolean flag ) {
        running = flag;
    }

    public void run() {
        alive = true;
        running = true;
        while ( alive ) {
            while ( running ) {
                system.iterate( dt );
                ThreadHelper.quietNap( waitTime );
            }
        }
    }

    System2D system;
    boolean running;
    boolean alive;
    double dt;
    int waitTime;
}
