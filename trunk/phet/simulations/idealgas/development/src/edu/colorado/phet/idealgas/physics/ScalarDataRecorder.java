/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:56:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.physics.PhysicalSystem;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Records dataTotal by recording events in which a scalar data is
 * recorded at specific time intervals.
 *
 * The recorder maintains an internal thread that computes the "dataTotal"
 * every 1/2 second.
 */
public class ScalarDataRecorder {
    private LinkedList dataRecord = new LinkedList();
    private float dataTotal;
    private float dataAverage;
    // Size, in milliseconds, of the sliding window over which samples are averaged
    private long timeWindow = 5000;


    public ScalarDataRecorder() {
        new PeriodicDataComputer().start();
    }

    /**
     *
     */
    public void clear() {
        dataRecord.removeAll( dataRecord );
    }

    /**
     *
     */
    public float getDataTotal() {
        return dataTotal;
    }

    /**
     *
     */
    public float getDataAverage() {
        return dataAverage;
    }

    private synchronized void computeDataStatistics( LinkedList dataRecord ) {
        long currTime = System.currentTimeMillis();

        // Remove entries from the data record that have aged out of the time window
        while( dataRecord.size() > 0
                && currTime - ((DataRecordEntry)dataRecord.get(0)).getTime() > timeWindow ) {
            dataRecord.remove(0);
        }

        float totalImpact = 0;
            for( int i = 0; i < dataRecord.size(); i++ ) {
                DataRecordEntry entry = (DataRecordEntry)dataRecord.get( i );
                    totalImpact += entry.getMomentum();
            }
        dataTotal = totalImpact;
        dataAverage = dataTotal / dataRecord.size();
    }

    /**
     *
     */
    public synchronized void addDataRecordEntry( float impact ) {
        DataRecordEntry entry = new DataRecordEntry(
                System.currentTimeMillis(), impact );
        dataRecord.addLast( entry );
    }

    public int getNumEntries() {
        return this.dataRecord.size();
    }

    //
    // Static fields and methods
    //


    //
    // Inner classes
    //

    /**
     * Class for entries in the time-based dataTotal record list
     */
    private class DataRecordEntry {

        private long time;
        private float momentum;

        DataRecordEntry( long time, float momentum ) {
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
    private class PeriodicDataComputer extends Thread {
        public void run() {
            while( true ) {
                try {
                    Thread.sleep( 500 );
                    computeDataStatistics( dataRecord );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
