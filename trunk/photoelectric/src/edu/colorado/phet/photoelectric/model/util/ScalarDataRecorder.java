/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model.util;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.EventListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * ScalarDataRecorder
 * <p/>
 * Records by logging events in which a scalar data is
 * recorded at specific time intervals.
 * <p/>
 * The recorder maintains an internal thread that computes the "dataTotal"
 * at intervals specified by the timeWindow. The default timeWindow is 5 ms.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class ScalarDataRecorder {

    private List dataRecord = new ArrayList();
    private double dataTotal;
    private double dataAverage;
    // Size, in milliseconds, of the sliding window over which samples are averaged
    private double timeWindow;
    private double timeSpanOfEntries;
    private AbstractClock clock;

    /**
     * @param clock        The simulation clock whose elapsed time is used to compute rates
     * @param updatePeriod The period, in real time msec, between which the data statistics
     *                     are updated
     * @param timeWindow   The sliding window during which data entries are kept. As they
     *                     age out of the window, they are discarded from the recorder. The
     *                     units are simulation clock ticks
     */
    public ScalarDataRecorder( AbstractClock clock, int updatePeriod, int timeWindow ) {
        this.clock = clock;
        this.timeWindow = timeWindow;
        new PeriodicDataComputer( updatePeriod ).start();
    }

    public void clear() {
        dataRecord.removeAll( dataRecord );
    }

    public double getDataTotal() {
        return dataTotal;
    }

    public double getDataAverage() {
        return dataAverage;
    }

    public double getTimeWindow() {
        return timeWindow;
    }

    /**
     * Reports the time, in msec, between the first and last data entries
     * used in the last call to computeDataStatistics()
     *
     * @return
     */
    public double getTimeSpanOfEntries() {
        return timeSpanOfEntries;
    }

    private synchronized void computeDataStatistics() {
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

        dataTotal = 0;
        for( int i = 0; i < dataRecord.size(); i++ ) {
            DataRecordEntry entry = (DataRecordEntry)dataRecord.get( i );
            dataTotal += entry.getValue();
        }
        dataAverage = 0;
        if( dataRecord.size() > 0 ) {
            double timeOfFirstEntry = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
            double timeOfLastEntry = ( (DataRecordEntry)dataRecord.get( dataRecord.size() - 1 ) ).getTime();
            timeSpanOfEntries = timeOfLastEntry - timeOfFirstEntry;
            dataAverage = dataTotal / dataRecord.size();
        }
    }

    /**
     *
     */
    public synchronized void addDataRecordEntry( double value ) {
        DataRecordEntry entry = new DataRecordEntry( clock.getRunningTime(), value );
        dataRecord.add( entry );
    }

    public int getNumEntries() {
        return this.dataRecord.size();
    }

    public void setTimeWindow( double timeWindow ) {
        this.timeWindow = timeWindow;
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Data structure for entries in the time-based dataTotal record list
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
     * An internal timer-driven class that computes data statistics and sends out
     * update events on regular intervals
     */
    private class PeriodicDataComputer {
        Timer timer;
        AbstractClock clock;

        public PeriodicDataComputer( int updatePeriod ) {
            this.timer = new Timer( updatePeriod, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    computeDataStatistics();
                    synchronized( updateListenerChannel ) {
                        updateListenerProxy.update( new UpdateEvent( ScalarDataRecorder.this ) );
                    }
                }
            } );
        }

        public void start() {
            timer.start();
        }
    }

    //----------------------------------------------------------------
    // Events
    //----------------------------------------------------------------
    public class UpdateEvent extends EventObject {
        public UpdateEvent( Object source ) {
            super( source );
        }

        public ScalarDataRecorder getScalarDataRecorder() {
            return (ScalarDataRecorder)getSource();
        }
    }

    public interface UpdateListener extends EventListener {
        public void update( UpdateEvent event );
    }

    private EventChannel updateListenerChannel = new EventChannel( UpdateListener.class );
    private UpdateListener updateListenerProxy = (UpdateListener)updateListenerChannel.getListenerProxy();

    public void addUpdateListener( UpdateListener listener ) {
        updateListenerChannel.addListener( listener );
    }

    public void removeUpdateListener( UpdateListener listener ) {
        updateListenerChannel.removeListener( listener );
    }
}
