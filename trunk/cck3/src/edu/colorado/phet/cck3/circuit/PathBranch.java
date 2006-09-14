/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.model.Branch;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 9:22:33 PM
 * Copyright (c) Jun 13, 2004 by Sam Reid
 */
public class PathBranch extends Branch {
    protected ArrayList segments = new ArrayList();
    private Point2D.Double startPoint;

    public PathBranch( CircuitChangeListener listener, Junction startJunction, Junction endJunction ) {
        super( listener, startJunction, endJunction );
    }

    public Point2D getPosition( double x ) {
        Location seg = getLocation( x );
        return seg.getPoint2D();
    }

    public class Location {
        private Segment segment;
        private double distAlongSegment;

        public Location( Segment segment, double distAlongSegment ) {
            this.segment = segment;
            this.distAlongSegment = distAlongSegment;
        }

        public Segment getSegment() {
            return segment;
        }

        public double getDistAlongSegment() {
            return distAlongSegment;
        }

        public Point2D getPoint2D() {
            AbstractVector2D vec = new Vector2D.Double( segment.getStart(), segment.getEnd() );
            vec = vec.getInstanceOfMagnitude( distAlongSegment );
            return vec.getDestination( segment.getStart() );
        }
    }

    public Location getLocation( double x ) {
        double segStartsAt = 0;
        for( int i = 0; i < numSegments(); i++ ) {
            Segment seg = segmentAt( i );
            double segStopsAt = segStartsAt + seg.getLength();
            if( x <= segStopsAt ) {
                return new Location( seg, x - segStartsAt );
            }
            segStartsAt += seg.getLength();
        }
        return null;
    }

    public int numSegments() {
        return segments.size();
    }

    public Segment segmentAt( int i ) {
        return (Segment)segments.get( i );
    }

    public static class Segment {
        Point2D start;
        Point2D end;

        public Segment( Point2D start, Point2D end ) {
            this.start = start;
            this.end = end;
            if( Double.isNaN( start.getX() ) || Double.isNaN( start.getY() ) ) {
                throw new RuntimeException( "Start was NaN: " + start );
            }
            if( Double.isNaN( end.getX() ) || Double.isNaN( end.getY() ) ) {
                throw new RuntimeException( "end was NaN: " + end );
            }
        }

        public Point2D getStart() {
            return start;
        }

        public Point2D getEnd() {
            return end;
        }

        public double getLength() {
            double dist = start.distance( end );
            if( Double.isNaN( dist ) ) {
                throw new RuntimeException( " Length was NaN." );
            }
            return dist;
        }
    }

    public double getLength() {
        double length = 0;
        for( int i = 0; i < segments.size(); i++ ) {
            Segment segment = (Segment)segments.get( i );
            length += segment.getLength();
        }
        if( Double.isNaN( length ) ) {
            throw new RuntimeException( "Length is NaN" );
        }
        return length;
    }

    public void reset( Point2D start, Point2D next ) {
        segments.clear();
        Segment seg = new Segment( start, next );
        segments.add( seg );
    }

    public void addPoint( Point2D position ) {
        Segment seg = new Segment( lastPoint(), position );
        segments.add( seg );
    }

    public Point2D lastPoint() {
        return segmentAt( numSegments() - 1 ).getEnd();
    }

    public void addPoint( AbstractVector2D vec ) {
        Point2D pt = vec.getDestination( lastPoint() );
        addPoint( pt );
    }

}
