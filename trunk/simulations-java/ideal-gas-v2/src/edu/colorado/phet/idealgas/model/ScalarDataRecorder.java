/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:56:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

//import edu.colorado.phet.model.PhysicalSystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Records by recording events in which a scalar data is
 * recorded at specific time intervals.
 * <p/>
 * The recorder maintains an internal thread that computes the "dataTotal"
 * every 1/2 second.
 */
public class ScalarDataRecorder {
    private LinkedList dataRecord = new LinkedList();
    private float dataTotal;
    private float dataAverage;
    // Size, in milliseconds, of the sliding window over which samples are averaged
    private float timeWindow = 5;
    //    private long timeWindow = 5000;
    private float timeSpanOfEntries;
    //    private long timeSpanOfEntries;

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

    public float getTimeWindow() {
        //    public long getTimeWindow() {
        return timeWindow;
    }

    public float getTimeSpanOfEntries() {
        //    public long getTimeSpanOfEntries() {
        return timeSpanOfEntries;
    }

    private synchronized void computeDataStatistics( LinkedList dataRecord ) {
        //        long currTime = System.currentTimeMillis();
        float currTime = PhysicalSystem.instance().getRunningTime();

        // Remove entries from the data record that have aged out of the time window
        if( dataRecord.size() > 0 ) {
            float startTime = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
            while( dataRecord.size() > 0 && currTime - startTime > timeWindow ) {
                dataRecord.remove( 0 );
                if( dataRecord.size() > 0 ) {
                    startTime = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
                }
            }
        }

        float total = 0;
        for( int i = 0; i < dataRecord.size(); i++ ) {
            DataRecordEntry entry = (DataRecordEntry)dataRecord.get( i );
            total += entry.getValue();
        }
        if( dataRecord.size() > 0 ) {
            System.out.println( "dataRecord.size() = " + dataRecord.size() );
            float timeOfFirstEntry = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
            float timeOfLastEntry = ( (DataRecordEntry)dataRecord.get( dataRecord.size() - 1 ) ).getTime();
            //            long timeOfFirstEntry = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
            //            long timeOfLastEntry = ( (DataRecordEntry)dataRecord.get( dataRecord.size() - 1 ) ).getTime();
            timeSpanOfEntries = timeOfLastEntry - timeOfFirstEntry;
        }

        dataTotal = total;
        dataAverage = dataTotal / dataRecord.size();
        //        System.out.println( "dataTotal: " + dataTotal + "  dataAverage: " + dataAverage + "  size: " + dataRecord.size() );
    }

    /**
     *
     */
    public synchronized void addDataRecordEntry( double value ) {
//    public synchronized void addDataRecordEntry( float value ) {
        DataRecordEntry entry = new DataRecordEntry( PhysicalSystem.instance().getRunningTime(), value );
        //        DataRecordEntry entry = new DataRecordEntry( System.currentTimeMillis(), value );
        dataRecord.addLast( entry );
    }

    public int getNumEntries() {
        return this.dataRecord.size();
    }


    //
    // Inner classes
    //

    /**
     * Class for entries in the time-based dataTotal record list
     */
    private class DataRecordEntry {

        private float time;
        //        private long time;
        private float value;

        DataRecordEntry( float time, float momentum ) {
            //        DataRecordEntry( long time, float momentum ) {
            this.time = time;
            this.value = momentum;
        }

        public float getTime() {
            //        public long getTime() {
            return time;
        }

        public float getValue() {
            return value;
        }
    }

    /**
     *
     */
    private class PeriodicDataComputer {
        Timer timer;

        public PeriodicDataComputer() {
            this.timer = new Timer( 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    computeDataStatistics( dataRecord );
                }
            } );
        }

        public synchronized void start() {
            //            if( true ) return;
            timer.start();
        }

        //        public void run() {
        //            while( true ) {
        //                try {
        //                    //                    Thread.sleep( 1000 );
        //                    Thread.sleep( 1000 );
        //                    computeDataStatistics( dataRecord );
        //                }
        //                catch( InterruptedException e ) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }
    }
}
