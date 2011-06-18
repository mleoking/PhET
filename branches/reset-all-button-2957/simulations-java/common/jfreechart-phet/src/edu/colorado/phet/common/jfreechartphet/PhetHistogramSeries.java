// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.jfreechartphet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.general.Series;

/**
 * PhetHistogramSeries is a JFreeChart series for histogram observations.
 * <p/>
 * A histogram series has an immutable range, which is divided into equal-width bins.
 * When a one-dimensional data point (referred to as an observation) is added to a series,
 * it is placed in the bin that corresponds to its value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetHistogramSeries extends Series {

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * PhetHistogramBin describes a bin in a histogram.
     * A bin has start and end boundaries, and a count of the number of
     * observations that have fallen in the range between its boundaries.
     */
    private static class PhetHistogramBin {

        private final double startBoundary;
        private final double endBoundary;
        private int numberOfObservations; // mutable

        /* Creates an empty bin */
        public PhetHistogramBin( double startBoundary, double endBoundary ) {
            this.startBoundary = startBoundary;
            this.endBoundary = endBoundary;
            this.numberOfObservations = 0;
        }

        public double getStartBoundary() {
            return startBoundary;
        }

        public double getEndBoundary() {
            return endBoundary;
        }

        public int getNumberOfObservations() {
            return numberOfObservations;
        }

        public void increment() {
            numberOfObservations++;
        }

        public void clear() {
            numberOfObservations = 0;
        }

        public String toString() {
            return "startBoundary=" + startBoundary + " endBoundary=" + endBoundary + " numberOfObservations=" + numberOfObservations;
        }
    }

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final double minimum; // lower boundary for the series
    private final double maximum; // upper boundary for the series
    private final double binWidth; // width of all bins
    private int numberOfObservations; // improves performance for large number of bins
    private final List bins; // list of HistogramBin
    private boolean ignoreOutOfRangeObservations; // how to handle observations that are out of bounds

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructs an empty histogram series.
     *
     * @param key
     * @param minimum
     * @param maximum
     * @param numberOfBins
     */
    public PhetHistogramSeries( Comparable key, double minimum, double maximum, int numberOfBins ) {
        this( key, minimum, maximum, numberOfBins, null /* observations */ );
    }

    /**
     * Constructs a histogram series that contains some observations.
     *
     * @param key
     * @param minimum
     * @param maximum
     * @param numberOfBins
     * @param observations
     */
    public PhetHistogramSeries( Comparable key, double minimum, double maximum, int numberOfBins, double[] observations ) {
        super( key );

        if ( key == null ) {
            throw new IllegalArgumentException( "key is null" );
        }
        if ( !( minimum < maximum ) ) {
            throw new IllegalArgumentException( "minimum must be < maximum" );
        }
        if ( !( numberOfBins >= 1 ) ) {
            throw new IllegalArgumentException( "numberOfBins must be >= 1" );
        }

        this.minimum = minimum;
        this.maximum = maximum;
        this.binWidth = ( maximum - minimum ) / numberOfBins;
        this.ignoreOutOfRangeObservations = true;

        // create a set of empty bins
        this.numberOfObservations = 0;
        double startBoundary = minimum;
        double endBoundary;
        List bins = new ArrayList( numberOfBins );
        for ( int i = 0; i < numberOfBins; i++ ) {
            // Set the last bin's upper boundary to the maximum to avoid precision issues.
            if ( i == numberOfBins - 1 ) {
                endBoundary = maximum;
            }
            else {
                endBoundary = minimum + ( i + 1 ) * binWidth;
            }
            bins.add( new PhetHistogramBin( startBoundary, endBoundary ) );
            startBoundary = endBoundary;
            endBoundary = startBoundary + binWidth;
        }
        this.bins = bins;

        // fill the bins
        if ( observations != null ) {
            for ( int i = 0; i < observations.length; i++ ) {
                addObservation( observations[i] );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public int getNumberOfBins() {
        return bins.size();
    }

    public double getBinWidth() {
        return binWidth;
    }

    public int getNumberOfObservations() {
        return numberOfObservations;
    }

    public void setIgnoreOutOfRangeObservations( boolean ignoreOutOfRangeObservations ) {
        this.ignoreOutOfRangeObservations = ignoreOutOfRangeObservations;
    }

    public boolean getIgnoreOutOfRangeObservations() {
        return ignoreOutOfRangeObservations;
    }

    public int getNumberOfObservations( int binIndex ) {
        return getBin( binIndex ).getNumberOfObservations();
    }

    public double getStartBoundary( int binIndex ) {
        return getBin( binIndex ).getStartBoundary();
    }

    public double getEndBoundary( int binIndex ) {
        return getBin( binIndex ).getEndBoundary();
    }

    /**
     * Looks at all the bins and finds the maximum number of observations in a bin.
     * This is useful for adjusting the range of axes.
     *
     * @return int
     */
    public int getMaxObservations() {
        int maxBinSize = 0;
        final int numberOfBins = getNumberOfBins();
        for ( int i = 0; i < numberOfBins; i++ ) {
            int binSize = getBin( i ).getNumberOfObservations();
            if ( binSize > maxBinSize ) {
                maxBinSize = binSize;
            }
        }
        return maxBinSize;
    }

    //----------------------------------------------------------------------------
    // Bin management
    //----------------------------------------------------------------------------

    /*
    * Gets a bin.
    *
    * @param binIndex
    */

    private PhetHistogramBin getBin( int binIndex ) {
        return (PhetHistogramBin) bins.get( binIndex );
    }

    /**
     * Adds an observation to the proper bin.
     * Notifies all SeriesChangedListeners.
     *
     * @param observation
     * @throws IllegalArgumentException if the observation is out of range and getIgnoreOutOfRangeObservations if false
     */
    public void addObservation( double observation ) {
        addObservation( observation, true );
    }

    /**
     * Adds an observation to the proper bin.
     *
     * @param observation
     * @param notifyListeners whether to notify all SeriesChangeListeners
     * @throws IllegalArgumentException if the observation is out of range and getIgnoreOutOfRangeObservations if false
     */
    public void addObservation( double observation, boolean notifyListeners ) {
        if ( observation >= minimum && observation <= maximum ) {
            final int numberOfBins = getNumberOfBins();
            double fraction = ( observation - minimum ) / ( maximum - minimum );
            if ( fraction < 0.0 ) {
                fraction = 0.0;
            }
            int binIndex = (int) ( fraction * numberOfBins );
            // rounding could result in binIndex being out of bounds
            if ( binIndex >= numberOfBins ) {
                binIndex = numberOfBins - 1;
            }
            getBin( binIndex ).increment();
            numberOfObservations++;
            if ( notifyListeners ) {
                fireSeriesChanged();
            }
        }
        else if ( !ignoreOutOfRangeObservations ) {
            throw new IllegalArgumentException( "series " + getKey() + " observation is out of range: " + observation );
        }
    }

    /**
     * Clears the series.
     * Notifies all SeriesChangedListeners.
     */
    public void clear() {
        // clear all bins
        Iterator i = bins.iterator();
        while ( i.hasNext() ) {
            ( (PhetHistogramBin) i.next() ).clear();
        }
        // clear the series
        numberOfObservations = 0;
        fireSeriesChanged();
    }
}
