package edu.colorado.phet.signalcircuit.electron.wire1d;

import edu.colorado.phet.signalcircuit.phys2d.DoublePoint;

import java.util.Vector;

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
        //edu.colorado.phet.util.Debug.traceln("Added segment: "+ws);
    }

    public DoublePoint getPosition( double dist ) {
        double length = getLength();
        while( dist > length ) {
            dist -= length;
        }
        while( dist < 0 ) {
            dist += length;
        }
        for( int i = 0; i < numSegments(); i++ ) {
            if( segmentAt( i ).contains( dist ) ) {
                //edu.colorado.phet.util.Debug.traceln("Contained at segment: "+i+", segment="+segmentAt(i));
                return segmentAt( i ).getPosition( dist );
            }
        }
        return null;
    }

    public DoublePoint getEndPoint() {
        if( segments.size() == 0 ) {
            throw new RuntimeException( "No wires specified." );
        }
        DoublePoint start = segmentAt( segments.size() - 1 ).getFinish();
        return start;
    }

    public void addRelative( double x, double y ) {
        DoublePoint endPoint = getEndPoint();
        add( endPoint.getX() + x, y + endPoint.getY() );
    }

    /**
     * Attach to the previous wire.
     */
    public void add( double x, double y ) {
        if( segments.size() == 0 ) {
            throw new RuntimeException( "No wires specified." );
        }
        DoublePoint start = segmentAt( segments.size() - 1 ).getFinish();
        DoublePoint end = new DoublePoint( x, y );
        double d = totalDistance();
        add( new WireSegment( start, end, d ) );
    }

    /**
     * Start the system.
     */
    public void add( double x, double y, double dx, double dy ) {
        if( segments.size() != 0 ) {
            throw new RuntimeException( "Already started." );
        }
        DoublePoint start = new DoublePoint( x, y );
        DoublePoint end = start.add( new DoublePoint( dx, dy ) );
        add( new WireSegment( start, end, 0 ) );
    }

    public double totalDistance() {
        double x = 0;
        for( int i = 0; i < numSegments(); i++ ) {
            x += segmentAt( i ).length();
        }
        return x;
    }

    public int numSegments() {
        return segments.size();
    }

    public WireSegment getLastSegment() {
        return segmentAt( numSegments() - 1 );
    }

    public WireSegment segmentAt( int i ) {
        return (WireSegment)segments.get( i );
    }
}
