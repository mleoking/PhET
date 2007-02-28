/*
 * Class: Clock
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 18, 2002
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.util.ThreadHelper;

import java.io.IOException;

/**
 *
 */
public class Clock implements Runnable {

    TimeStep timeStep;
    boolean isRunning = false;
    boolean isAlive = false;
    boolean isSingleStepEnabled = false;

    float dt;
    int waitTime;
    float timeLimit = Float.MAX_VALUE;
    float runningTime;

    public Clock( TimeStep timeStep ) {
        this( timeStep, s_defaultDt, s_defaultWaitTime );
    }

    public Clock( TimeStep timeStep, float dt, int waitTime ) {
        this.timeStep = timeStep;
        this.dt = dt;
        this.waitTime = waitTime;
    }

    public float getRunningTime() {
        return runningTime;
    }

    public float getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit( float timeLimit ) {
        this.timeLimit = timeLimit;
    }

    public float getDt() {
        return dt;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setDt( float dt ) {
        this.dt = dt;
    }

    public void setWaitTime( int waitTime ) {
        this.waitTime = waitTime;
    }

    public boolean isActiveAndRunning() {
        return isAlive && isRunning;
    }

    public synchronized void setSystem( TimeStep timeStep ) {
        this.timeStep = timeStep;
    }

    public synchronized void setTimeIncrement( float dt ) {
        this.dt = dt;
    }

    public void start() {
        Thread t = new Thread( this );
//        t.setPriority( Thread.MAX_PRIORITY );
        t.start();
    }

    public void setIsAlive( boolean b ) {
        this.isAlive = b;
    }

    public void setIsRunning( boolean b ) {
        this.isRunning = b;
    }

    public boolean isSingleStepEnabled() {
        return isSingleStepEnabled;
    }

    public void setSingleStepEnabled( boolean singleStepEnabled ) {
        isSingleStepEnabled = singleStepEnabled;
    }

    public void stop() {
        setIsRunning( false );
        setIsAlive( false );
    }

    public void reset() {
        stop();
        this.runningTime = 0;
    }

    public void run() {
        this.isAlive = true;
        this.isRunning = true;
        runningTime = 0;
        while( isAlive ) {
            while( isRunning ) {

                if( this.isSingleStepEnabled ) {
                    try {
                        System.in.read();
                    }
                    catch( IOException e ) {
                    }
                }

                timeStep.stepInTime( dt );
                runningTime += dt;
                if( timeLimit > 0 && runningTime >= timeLimit ) {
                    isAlive = false;
                    isRunning = false;
                }
                ThreadHelper.quietNap( waitTime );
            }
        }
    }

    //
    // Static fields and methods
    //
    private static float s_defaultDt = 0.1f;
    private static int s_defaultWaitTime = 10;
}



