/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:56:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.physics.PhysicalSystem;

import java.util.LinkedList;

/**
 * Records pressure by recording events in which momentum is
 * recorded at a specific time. This is really not pressure,
 * and when an instance of this class is asked to getPressure(),
 * the client must divided the result by the area over which the events
 * were recorded.
 *
 * The recorder maintains an internal thread that computes the "pressure"
 * every 1/2 second.
 */
public class PressureRecorder {
    private LinkedList pressureRecord = new LinkedList();
    private float pressure;
    // Size, in milliseconds, of the sliding window over which samples are averaged
    private long timeWindow = 5000;


    public PressureRecorder() {
        new PeriodicPressureComputer().start();
    }

    /**
     *
     */
    public void clear() {
        pressureRecord.removeAll( pressureRecord );
    }

    /**
     *
     */
    public float getPressure() {
        return pressure;
    }

    private synchronized float computePressure( LinkedList pressureRecord ) {
        long currTime = System.currentTimeMillis();
        while( pressureRecord.size() > 0
                && currTime - ((PressureRecordEntry)pressureRecord.get(0)).getTime() > timeWindow ) {
            pressureRecord.remove(0);
        }
        float totalImpact = 0;
            for( int i = 0; i < pressureRecord.size(); i++ ) {
                PressureRecordEntry entry = (PressureRecordEntry)pressureRecord.get( i );
                    totalImpact += entry.getMomentum();
            }
//            clear();
        return totalImpact;
    }

    /**
     *
     */
    public synchronized void addPressureRecordEntry( float impact ) {
        PressureRecordEntry entry = new PressureRecordEntry(
                System.currentTimeMillis(), impact );
        pressureRecord.addLast( entry );
    }

    //
    // Static fields and methods
    //


    //
    // Inner classes
    //

    /**
     * Class for entries in the time-based pressure record list
     */
    private class PressureRecordEntry {

        private long time;
        private float momentum;

        PressureRecordEntry( long time, float momentum ) {
            this.time = time;
            this.momentum = momentum;
        }

        public long getTime() {
            return time;
        }

        public float getMomentum() {
            return momentum;
        }
    }

    /**
     *
     */
    private class PeriodicPressureComputer extends Thread {
        public void run() {
            while( true ) {
                try {
                    Thread.sleep( 500 );
                    pressure = computePressure( pressureRecord );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
