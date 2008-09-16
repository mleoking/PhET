package edu.colorado.phet.ohm1d.common.wire1d;

import java.util.Vector;

import edu.colorado.phet.ohm1d.common.phys2d.DoublePoint;

/**
 * Represents a single path along wires and tools for creating that path.
 */
public class WirePatch {
    Vector segments;

    public WirePatch() {
        segments = new Vector();
    }

    public double getLength() {
        return segmentAt( segments.size() - 1 ).getFinishScalar();
    }

    public void add( WireSegment ws ) {
        segments.add( ws );
        //util.Debug.traceln("Added segment: "+ws);
    }

    public DoublePoint getPosition( double dist ) {
        double length = getLength();
        while ( dist > length ) {
            dist -= length;
        }
        while ( dist < 0 ) {
            dist += length;
        }
        for ( int i = 0; i < numSegments(); i++ ) {
            if ( segmentAt( i ).contains( dist ) ) {
                //util.Debug.traceln("Contained at segment: "+i+", segment="+segmentAt(i));
                return segmentAt( i ).getPosition( dist );
            }
        }
        return null;
    }

    /**
     * Attach to the previous wire.
     */
    public void add( double x, double y ) {
        if ( segments.size() == 0 ) {
            throw new RuntimeException( "No wires specified." );
        }
        DoublePoint start = segmentAt( segments.size() - 1 ).getFinish();
        DoublePoint end = new DoublePoint( x, y );
        double d = totalDistance();
        add( new WireSegment( start, end, d ) );
    }

    public void add( DoublePoint nextPoint ) {
        add( nextPoint.getX(), nextPoint.getY() );
    }

    public void start( DoublePoint a, DoublePoint b ) {
        add( a, b.subtract( a ) );
    }

    /**
     * Start the system.
     */
    public void add( double x, double y, double dx, double dy ) {
        if ( segments.size() != 0 ) {
            throw new RuntimeException( "Already started." );
        }
        DoublePoint start = new DoublePoint( x, y );
        DoublePoint end = start.add( new DoublePoint( dx, dy ) );
        add( new WireSegment( start, end, 0 ) );
    }

    /**
     * Start the system.
     */
    public void add( DoublePoint start, DoublePoint dx ) {
        add( start.getX(), start.getY(), dx.getX(), dx.getY() );
    }

    public double totalDistance() {
        double x = 0;
        for ( int i = 0; i < numSegments(); i++ ) {
            x += segmentAt( i ).length();
        }
        return x;
    }

    public int numSegments() {
        return segments.size();
    }

    public WireSegment segmentAt( int i ) {
        return (WireSegment) segments.get( i );
    }
}
