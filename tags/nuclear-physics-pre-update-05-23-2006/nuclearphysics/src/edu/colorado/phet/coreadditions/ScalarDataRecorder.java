/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.EventListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * ScalarDataRecorder
 * <p>
 * Records events in which a scalar datum is recorded at specific time intervals.
 * <p>
 * Data are collected over a sliding window of time whose width is settable. The default
 * width is 5 msec (simulation clock time).
 * <p>
 * The recorder computes the average of the recorded scalars on a periodic basis. The
 * default period is .5 sec (wall time).
 * <p>
 * Objects can subscribe for UpdateEvents from instances of this class by adding themselves
 * as implementers of ScalarDataRecorder.UpdateListener
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ScalarDataRecorder {
    private List dataRecord = new ArrayList();
    private double dataTotal;
    private double dataAverage;
    // Size, in milliseconds, of the sliding window over which samples are averaged
    private double timeWindow = 5;
    private double timeSpanOfEntries;
    private AbstractClock clock;

    public ScalarDataRecorder( AbstractClock clock ) {
        this.clock = clock;
    }

    /**
     *
     * @param clock
     * @param updatePeriod  The interval, in msec (wall time), between updates to the statistics
     * reported by the ScalarDataRecorder
     */
    public ScalarDataRecorder( AbstractClock clock, double timeWindow, int updatePeriod ) {
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
     * Object that computes the data statistics at speified intervals
     */
    private class PeriodicDataComputer {
        Timer timer;
        AbstractClock clock;

        /**
         *
         * @param updatePeriod The number of msec between times that the
         * data statistics are computed
         */
        public PeriodicDataComputer( int updatePeriod ) {
            this.timer = new Timer( updatePeriod, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    computeDataStatistics();
                    updateListenerproxy.update( new UpdateEvent( ScalarDataRecorder.this ) );
                }
            } );
        }

        public synchronized void start() {
            timer.start();
        }
    }

    //----------------------------------------------------------------
    // Events and listeners
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
        void update( UpdateEvent event );
    }

    private EventChannel updateEventChannel = new EventChannel( UpdateListener.class );
    private UpdateListener updateListenerproxy = (UpdateListener)updateEventChannel.getListenerProxy();

    public void addUpdateListener( UpdateListener listener ) {
        updateEventChannel.addListener( listener );
    }

    public void removeUpdateListener( UpdateListener listener ) {
        updateEventChannel.removeListener( listener );
    }
}
