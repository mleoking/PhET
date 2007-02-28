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

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.ModelEventChannel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;


/**
 * ScalarDataRecorder
 * <p/>
 * Records by logging scalar data with time stamps. At periodic intervals,
 * the recorder computes simple statistics on the data it has recorded, and sends update
 * events to listeners.
 * <p/>
 * Each time statistics are computed, data with timestamps older than a specified age,
 * the simulationTimeWindow, are thrown out.
 * <p/>
 * The recorder starts when its clientUpdateInterval is specified, either in a constructor
 * or in a call to setClientUpdateInterval()
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
    private IClock clock;
//    private AbstractClock clock;
    private PeriodicDataComputer periodicDataComputer;
    private double minVal;
    private double maxVal;

    /**
     * @param clock
     */
    public ScalarDataRecorder( IClock clock ) {
//    public ScalarDataRecorder( AbstractClock clock ) {
        this.clock = clock;
    }

    /**
     * @param clock                The simulation clock whose elapsed time is used to compute rates
     * @param clientUpdateInterval The period, in real time msec, between which the data statistics
     *                             are updated
     * @param simulationTimeWindow The sliding window during which data entries are kept. As they
     *                             age out of the window, they are discarded from the recorder. The
     *                             units are simulation clock ticks
     */
    public ScalarDataRecorder( IClock clock,
//    public ScalarDataRecorder( AbstractClock clock,
                               int clientUpdateInterval,
                               int simulationTimeWindow ) {
        this.clock = clock;
        this.timeWindow = simulationTimeWindow;
        periodicDataComputer = new PeriodicDataComputer( clientUpdateInterval );
        periodicDataComputer.start();
    }

    public void clear() {
        dataRecord.removeAll( dataRecord );
    }

    /**
     * Computes various statistics on the data collected during the last
     * simulation time window. Data older than that is discarded from the
     * record.
     */
    protected synchronized void computeDataStatistics() {
        double currTime = clock.getSimulationTime();
//        double currTime = clock.getRunningTime();

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
        minVal = Double.MAX_VALUE;
        maxVal = Double.MIN_VALUE;
        for( int i = 0; i < dataRecord.size(); i++ ) {
            DataRecordEntry entry = (DataRecordEntry)dataRecord.get( i );
            dataTotal += entry.getValue();

            // Track the range of the entries
            minVal = minVal > entry.getValue() ? entry.getValue() : minVal;
            maxVal = maxVal < entry.getValue() ? entry.getValue() : maxVal;
        }
        dataAverage = 0;
        if( dataRecord.size() > 0 ) {
            // Get the time span of the entries
            double timeOfFirstEntry = ( (DataRecordEntry)dataRecord.get( 0 ) ).getTime();
            double timeOfLastEntry = ( (DataRecordEntry)dataRecord.get( dataRecord.size() - 1 ) ).getTime();
            timeSpanOfEntries = timeOfLastEntry - timeOfFirstEntry;

            // Compute the average of the entries
            dataAverage = dataTotal / dataRecord.size();
        }
    }

    /**
     *
     */
    public synchronized void addDataRecordEntry( double value ) {
        DataRecordEntry entry = new DataRecordEntry( clock.getSimulationTime(), value );
//        DataRecordEntry entry = new DataRecordEntry( clock.getRunningTime(), value );
        dataRecord.add( entry );
    }

    //----------------------------------------------------------------
    // Setters and getters for recording parameters
    //----------------------------------------------------------------

    public double getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow( double timeWindow ) {
        this.timeWindow = timeWindow;
    }

    public void setClientUpdateInterval( int clientUpdateInterval ) {
        if( periodicDataComputer != null ) {
            periodicDataComputer.stop();
        }
        periodicDataComputer = new PeriodicDataComputer( clientUpdateInterval );
        periodicDataComputer.start();
    }

    //----------------------------------------------------------------
    // Getters for statistics of the data collected in the last call
    // computeDataStatistices
    //----------------------------------------------------------------

    /**
     * Returns the total of the data collected in the simulation time window
     * @return
     */
    public double getDataTotal() {
        return dataTotal;
    }

    /**
     * Returns the aveage of the data collected in the simulation time window
     * @return
     */
    public double getDataAverage() {
        return dataAverage;
    }

    /**
     * Returns the avearage of the data collected in the simulation time window
     * @return
     */
    public int getNumEntries() {
        return this.dataRecord.size();
    }

    /**
     * Reports the time, in msec, between the first and last data entries
     * used in the simulation time window
     *
     * @return
     */
    public double getTimeSpanOfEntries() {
        return timeSpanOfEntries;
    }

    /**
     * Returns the minimum value recorded in the simulation time window
     * @return
     */
    public double getMinVal() {
        return minVal;
    }

    /**
     * Returns the minimum value recorded in the simulation time window
     * @return
     */
    public double getMaxVal() {
        return maxVal;
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
        IClock clock;
//        AbstractClock clock;

        PeriodicDataComputer( int clientUpdateInterval ) {
            this.timer = new Timer( clientUpdateInterval, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    computeDataStatistics();
                    synchronized( updateListenerChannel ) {
                        updateListenerProxy.update( new UpdateEvent( ScalarDataRecorder.this ) );
                    }
                }
            } );
        }

        void start() {
            timer.start();
        }

        void stop() {
            timer.stop();
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
        public void update( UpdateEvent event );
    }

    private EventChannel updateListenerChannel = new ModelEventChannel( UpdateListener.class );
    private UpdateListener updateListenerProxy = (UpdateListener)updateListenerChannel.getListenerProxy();

    public void addUpdateListener( UpdateListener listener ) {
        updateListenerChannel.addListener( listener );
    }

    public void removeUpdateListener( UpdateListener listener ) {
        updateListenerChannel.removeListener( listener );
    }
}
