/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:56:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.util;


import edu.colorado.phet.common.model.clock.AbstractClock;

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
    private double dataTotal;
    private double dataAverage;
    // Size, in milliseconds, of the sliding window over which samples are averaged
    private double timeWindow = 5;
    //    private long timeWindow = 5000;
    private double timeSpanOfEntries;
    private AbstractClock clock;
    //    private long timeSpanOfEntries;

    public ScalarDataRecorder( AbstractClock clock ) {
        this.clock = clock;
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
    public double getDataTotal() {
        return dataTotal;
    }

    /**
     *
     */
    public double getDataAverage() {
        return dataAverage;
    }

    public double getTimeWindow() {
        return timeWindow;
    }

    public double getTimeSpanOfEntries() {
        return timeSpanOfEntries;
    }

    private synchronized void computeDataStatistics( LinkedList dataRecord ) {
        //        long currTime = System.currentTimeMillis();
        //        float currTime = PhysicalSystem.instance().getRunningTime();
        double currTime = clock.getRunningTime();

        // Remove entries from the data record that have aged out of the time window
        if( dataRecord.size() > 0 ) {
            double startTime = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
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
            double timeOfFirstEntry = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
            double timeOfLastEntry = ( (DataRecordEntry)dataRecord.get( dataRecord.size() - 1 ) ).getTime();
            timeSpanOfEntries = timeOfLastEntry - timeOfFirstEntry;
        }

        dataTotal = total;
        dataAverage = dataTotal / dataRecord.size();
    }

    /**
     *
     */
    public synchronized void addDataRecordEntry( double value ) {
        DataRecordEntry entry = new DataRecordEntry( clock.getRunningTime(), value );
        //        DataRecordEntry entry = new DataRecordEntry( PhysicalSystem.instance().getRunningTime(), value );
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

        private double time;
        private double value;

        DataRecordEntry( double time, double value ) {
            this.time = time;
            this.value = value;
        }

        public double getTime() {
            return time;
        }

        public double getValue() {
            return value;
        }
    }

    /**
     *
     */
    private class PeriodicDataComputer {
        Timer timer;
        AbstractClock clock;

        public PeriodicDataComputer() {
            this.timer = new Timer( 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    computeDataStatistics( dataRecord );
                }
            } );
        }

        public synchronized void start() {
            timer.start();
        }
    }
}
